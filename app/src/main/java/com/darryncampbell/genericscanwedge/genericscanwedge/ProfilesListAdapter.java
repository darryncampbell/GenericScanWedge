package com.darryncampbell.genericscanwedge.genericscanwedge;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by darry on 13/08/2016.
 */
//  Adapter for the list of profiles available to be configured.
public class ProfilesListAdapter extends ArrayAdapter<Profile> {
    private final Context context;
    private final ArrayList<Profile> values;

    public ProfilesListAdapter(Context context, ArrayList<Profile> values)
    {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.profile_list_detail, parent, false);
        TextView textViewTitle = (TextView) rowView.findViewById(R.id.regionProfileName);
        textViewTitle.setText(values.get(position).getName());
        textViewTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                Intent configureProfile = new Intent(context, ProfileConfiguration.class);
                configureProfile.putExtra("profileObjects", (Serializable) values);
                configureProfile.putExtra("profilePosition", position);
                context.startActivity(configureProfile);
            }
            public void onNothingSelected(AdapterView parentView) {}
        });

        return rowView;
    }
}
