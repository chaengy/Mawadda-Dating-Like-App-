package com.example.mawadda3_0;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mMatchPlan, mMatchName,mMatchAge,mMatchHobby,mMatchEducation,mMatchId;
    public ImageView mMatchImage;
    public MatchesViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mMatchName = itemView.findViewById(R.id.MatchName);
        mMatchAge = itemView.findViewById(R.id.Matchage);
        mMatchHobby = itemView.findViewById(R.id.Matchhobby);
        mMatchEducation = itemView.findViewById(R.id.Matcheducation);
        mMatchPlan = itemView.findViewById(R.id.Matchplan);
        mMatchImage = itemView.findViewById(R.id.MatchImage);
        mMatchId = itemView.findViewById(R.id.Matchid);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mMatchId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
