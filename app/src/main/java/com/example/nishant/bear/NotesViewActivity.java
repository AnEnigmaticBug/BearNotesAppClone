package com.example.nishant.bear;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class NotesViewActivity extends AppCompatActivity {

    DatabaseReference metaDataDirRef = FirebaseDatabase.getInstance().getReference().child("MetaData");
    DatabaseReference tagDirRef = FirebaseDatabase.getInstance().getReference().child("Tags");
    List<NoteMetaData> completeNoteMetaDataList = new ArrayList<>();
    List<NoteMetaData> filteredNoteMetaDataList = new ArrayList<>();
    List<String> completeTagList = new ArrayList<>();

    private String currentTag = "#Notes";
    private TextView currentTagNameLBL;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_view);

        currentTagNameLBL = findViewById(R.id.current_tag_name_lbl);
        currentTagNameLBL.setText(currentTag);

        drawerLayout = findViewById(R.id.drawer_layout);

        final RecyclerView notesRCY = findViewById(R.id.notes_rcy);
        notesRCY.setAdapter(new NotesRecyclerAdapter());
        notesRCY.setLayoutManager(new LinearLayoutManager(this));

        final RecyclerView tagsRCY = findViewById(R.id.tags_rcy);
        tagsRCY.setAdapter(new TagsRecyclerAdapter());
        tagsRCY.setLayoutManager(new LinearLayoutManager(this));

        metaDataDirRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                completeNoteMetaDataList.clear();
                filteredNoteMetaDataList.clear();
                for(DataSnapshot userData: dataSnapshot.getChildren()) {
                    NoteMetaData metaData = new NoteMetaData();
                    if(!userData.hasChild("Title")) {
                        Log.d("Firebase", "Title DNE");
                    }
                    metaData.setId(Integer.parseInt(userData.getKey()));
                    metaData.setTitle((String)userData.child("Title").getValue());
                    metaData.setPreview((String)userData.child("Preview").getValue());
                    metaData.setDate((String)userData.child("DateModified").getValue());
                    List<String> tags = new ArrayList<>();
                    for(DataSnapshot tagData : userData.child("Tags").getChildren()) {
                        tags.add((String)tagData.getValue());
                    }
                    metaData.setTags(tags);
                    completeNoteMetaDataList.add(metaData);
                }
                Collections.sort(completeNoteMetaDataList, new Comparator<NoteMetaData>() {
                    @Override
                    public int compare(NoteMetaData d1, NoteMetaData d2) {
                        if(!d1.getDate().substring(6, 8).equals(d2.getDate().substring(6, 8))) {
                            return -d1.getDate().substring(6, 8).compareTo(d2.getDate().substring(6, 8));
                        }
                        if(!d1.getDate().substring(3, 5).equals(d2.getDate().substring(3, 5))) {
                            return -d1.getDate().substring(3, 5).compareTo(d2.getDate().substring(3, 5));
                        }
                        return -d1.getDate().compareTo(d2.getDate());
                    }
                });
                changeCurrentTag(currentTag);
                ((NotesRecyclerAdapter) notesRCY.getAdapter()).setMetaDataList(filteredNoteMetaDataList);
                notesRCY.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        tagDirRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                completeTagList.clear();
                for(DataSnapshot tagData: dataSnapshot.getChildren()) {
                    String tagName = (String)tagData.getValue();
                    if(!(tagName.equals("#Notes") || tagName.equals("#Trash"))) {
                        completeTagList.add((String)tagData.getValue());
                    }
                }
                ((TagsRecyclerAdapter)tagsRCY.getAdapter()).setTagList(completeTagList);
                tagsRCY.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onMenuBTNClicked(View view) {
        drawerLayout.openDrawer(Gravity.START);
    }

    public void onSearchBTNClicked(View view) {
        ImageButton menuBTN = findViewById(R.id.menu_btn);
        menuBTN.setVisibility(View.INVISIBLE);
        TextView currentTagNameLBL = findViewById(R.id.current_tag_name_lbl);
        currentTagNameLBL.setVisibility(View.INVISIBLE);
        ImageButton searchBTN = findViewById(R.id.search_btn);
        searchBTN.setVisibility(View.INVISIBLE);
        Button cancelSearchBTN = findViewById(R.id.cancel_search_btn);
        cancelSearchBTN.setVisibility(View.VISIBLE);
        EditText searchFieldTXT = findViewById(R.id.search_field_txt);
        searchFieldTXT.setVisibility(View.VISIBLE);
    }

    public void onCancelSearchBTNClicked(View view) {
        ImageButton menuBTN = findViewById(R.id.menu_btn);
        menuBTN.setVisibility(View.VISIBLE);
        TextView currentTagNameLBL = findViewById(R.id.current_tag_name_lbl);
        currentTagNameLBL.setVisibility(View.VISIBLE);
        ImageButton searchBTN = findViewById(R.id.search_btn);
        searchBTN.setVisibility(View.VISIBLE);
        Button cancelSearchBTN = findViewById(R.id.cancel_search_btn);
        cancelSearchBTN.setVisibility(View.GONE);
        EditText searchFieldTXT = findViewById(R.id.search_field_txt);
        searchFieldTXT.setVisibility(View.GONE);
        searchFieldTXT.setText("");
        searchFieldTXT.clearFocus();
    }

    public void onAddNoteBTNClicked(View view) {
        Intent intent = new Intent();
        intent.setClass(this, NoteEditActivity.class);
        intent.putExtra("noteID", -1L);
        startActivity(intent);
    }

    public void onNotesBarLINClicked(View view) {
        changeCurrentTag("#Notes");
    }

    public void onTrashBarLINClicked(View view) {
        changeCurrentTag("#Trash");

    }

    public void onSettingsBTNClicked(View view) {
        Intent intent = new Intent();
        intent.setClass(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void changeCurrentTag(String newTag) {
        currentTag = newTag;
        currentTagNameLBL.setText(currentTag);
        drawerLayout.closeDrawer(Gravity.START);
        filteredNoteMetaDataList.clear();
        for(NoteMetaData noteMetaData : completeNoteMetaDataList) {
            if(noteMetaData.getTags().contains(currentTag)) {
                filteredNoteMetaDataList.add(noteMetaData);
            }
        }
        RecyclerView notesRCY = findViewById(R.id.notes_rcy);
        ((NotesRecyclerAdapter)notesRCY.getAdapter()).setMetaDataList(filteredNoteMetaDataList);
        notesRCY.getAdapter().notifyDataSetChanged();
    }

    private void removeTag(final String tag) {
        for(int i = 0; i < completeTagList.size(); ++i) {
            if(completeTagList.get(i).equals(tag)) {
                completeTagList.remove(i);
                RecyclerView tagsRCY = findViewById(R.id.tags_rcy);
                tagsRCY.getAdapter().notifyDataSetChanged();
                break;
            }
        }
        tagDirRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot tagData : dataSnapshot.getChildren()) {
                    if(tagData.getValue().equals(tag)) {
                        tagData.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder> {

        private List<NoteMetaData> metaDataList = new ArrayList<>();

        void setMetaDataList(List<NoteMetaData> metaDataList) {
            if(metaDataList == null) {
                this.metaDataList = new ArrayList<>();
            }
            this.metaDataList = metaDataList;
        }

        @Override
        public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NotesViewHolder(getLayoutInflater().inflate(R.layout.row_notes_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(NotesViewHolder holder, int position) {
            holder.getDateModifiedLBL().setText(getDateText(metaDataList.get(position).getDate()));
            holder.getTitleLBL().setText(metaDataList.get(position).getTitle());
            holder.getPreviewLBL().setText(metaDataList.get(position).getPreview());
        }

        @Override
        public int getItemCount() {
            return metaDataList.size();
        }

        private String getDateText(String date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
            Date todaysDate = new Date();
            try {
                Date modificationDate = dateFormat.parse(date);
                long diff = todaysDate.getTime() - modificationDate.getTime();
                long dayDiff = ((diff / 1000) / 3600) / 24;
                if(dayDiff > 365) {
                    long yearDiff = dayDiff / 365;
                    return String.valueOf(yearDiff) + "Y";
                }
                if(dayDiff > 30) {
                    long monthDiff = dayDiff / 30;
                    return String.valueOf(monthDiff) + "M";
                }
                if(dayDiff > 7) {
                    long weekDiff = dayDiff / 7;
                    return String.valueOf(weekDiff) + "W";
                }
                return String.valueOf(dayDiff) + "D";
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return "?C";
        }

        class NotesViewHolder extends RecyclerView.ViewHolder {

            private TextView dateModifiedLBL;
            private TextView titleLBL;
            private TextView previewLBL;

            public NotesViewHolder(final View itemView) {
                super(itemView);
                View noteTileCTL = itemView.findViewById(R.id.note_tile_ctl);
                dateModifiedLBL = noteTileCTL.findViewById(R.id.date_modified_lbl);
                titleLBL = noteTileCTL.findViewById(R.id.title_lbl);
                previewLBL = noteTileCTL.findViewById(R.id.preview_lbl);
                noteTileCTL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(view.getContext(), NoteEditActivity.class);
                        intent.putExtra("noteID", metaDataList.get(getAdapterPosition()).getId());
                        startActivity(intent);
                    }
                });
                View swipeMenuCTL = itemView.findViewById(R.id.swipe_menu_ctl);
                ImageButton shareNoteBTN = swipeMenuCTL.findViewById(R.id.share_note_btn);
                shareNoteBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((SwipeRevealLayout)itemView).close(true);
                    }
                });
                ImageButton deleteNoteBTN = swipeMenuCTL.findViewById(R.id.delete_note_btn);
                deleteNoteBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((SwipeRevealLayout)itemView).close(true);
                        long id = filteredNoteMetaDataList.get(getAdapterPosition()).getId();
                        List<String> tags = filteredNoteMetaDataList.get(getAdapterPosition()).getTags();
                        for(int i = 0; i < completeNoteMetaDataList.size(); ++i) {
                            if(completeNoteMetaDataList.get(i).getId() == id) {
                                completeNoteMetaDataList.remove(i);
                                break;
                            }
                        }
                        changeCurrentTag(currentTag);
                        for(String tag : tags) {
                            Boolean found = false;
                            for(NoteMetaData metaData : completeNoteMetaDataList) {
                                if(metaData.getTags().contains(tag)) {
                                    found = true;
                                    break;
                                }
                            }
                            if(!found) {
                                removeTag(tag);
                            }
                        }
                        metaDataDirRef.child(String.valueOf(id)).removeValue();
                    }
                });
            }

            public TextView getDateModifiedLBL() {
                return dateModifiedLBL;
            }

            public TextView getTitleLBL() {
                return titleLBL;
            }

            public TextView getPreviewLBL() {
                return previewLBL;
            }
        }
    }

    class TagsRecyclerAdapter extends RecyclerView.Adapter<TagsRecyclerAdapter.TagsViewHolder> {

        List<String> tagList = new ArrayList<>();

        public void setTagList(List<String> tagList) {
            if(tagList == null) {
                Log.d("Firebase", "Null Tag List");
            }
            else {
                this.tagList = tagList;
            }
        }

        @Override
        public TagsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TagsViewHolder(getLayoutInflater().inflate(R.layout.row_tags_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(TagsViewHolder holder, int position) {
            holder.getTagNameLBL().setText(tagList.get(position));
        }

        @Override
        public int getItemCount() {
            return tagList.size();
        }

        class TagsViewHolder extends RecyclerView.ViewHolder {

            private TextView tagNameLBL;

            public TagsViewHolder(View itemView) {
                super(itemView);
                tagNameLBL = itemView.findViewById(R.id.tag_name_lbl);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeCurrentTag(((TextView)view.findViewById(R.id.tag_name_lbl)).getText().toString());
                    }
                });
            }

            public TextView getTagNameLBL() {
                return tagNameLBL;
            }
        }
    }
}
