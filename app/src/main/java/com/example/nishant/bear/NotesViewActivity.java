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

import java.util.ArrayList;
import java.util.List;

public class NotesViewActivity extends AppCompatActivity {

    DatabaseReference metaDataDirRef = FirebaseDatabase.getInstance().getReference().child("MetaData");
    List<NoteMetaData> noteMetaDataList = new ArrayList<>();

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
        metaDataDirRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userData: dataSnapshot.getChildren()) {
                    NoteMetaData metaData = new NoteMetaData();
                    if(!userData.hasChild("Title")) {
                        Log.d("Firebase", "Title DNE");
                    }
                    metaData.setTitle((String)userData.child("Title").getValue());
                    metaData.setPreview((String)userData.child("Preview").getValue());
                    noteMetaDataList.add(metaData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        RecyclerView notesRCY = findViewById(R.id.notes_rcy);
        notesRCY.setAdapter(new NotesRecyclerAdapter());
        ((NotesRecyclerAdapter)notesRCY.getAdapter()).setMetaDataList(noteMetaDataList);
        notesRCY.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView tagsRCY = findViewById(R.id.tags_rcy);
        tagsRCY.setAdapter(new TagsRecyclerAdapter());
        tagsRCY.setLayoutManager(new LinearLayoutManager(this));
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
    }

    class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder> {

        private List<NoteMetaData> metaDataList;

        void setMetaDataList(List<NoteMetaData> metaDataList) {
            this.metaDataList = metaDataList;
        }

        @Override
        public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NotesViewHolder(getLayoutInflater().inflate(R.layout.row_notes_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(NotesViewHolder holder, int position) {
            holder.getDateModifiedLBL().setText("3M");
            holder.getTitleLBL().setText(metaDataList.get(position).getTitle());
            holder.getPreviewLBL().setText(metaDataList.get(position).getPreview());
        }

        @Override
        public int getItemCount() {
            return metaDataList.size();
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

        @Override
        public TagsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TagsViewHolder(getLayoutInflater().inflate(R.layout.row_tags_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(TagsViewHolder holder, int position) {
            holder.getTagNameLBL().setText("# TagName" + position);
        }

        @Override
        public int getItemCount() {
            return 10;
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
