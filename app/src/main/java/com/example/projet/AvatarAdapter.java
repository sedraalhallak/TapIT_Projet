package com.example.projet;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class AvatarAdapter extends BaseAdapter {
    private Context context;
    private int[] avatarIds;
    private int selectedId;

    public AvatarAdapter(Context context, int[] avatarIds, int selectedId) {
        this.context = context;
        this.avatarIds = avatarIds;
        this.selectedId = selectedId;
    }

    @Override
    public int getCount() {
        return avatarIds.length;
    }

    @Override
    public Object getItem(int position) {
        return avatarIds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
       ImageView imageView;
       if (convertView == null) {
           imageView = new ImageView(context);
           // Dimensions fixes pour chaque item
           int size = calculateCellSize(context);
           imageView.setLayoutParams(new GridView.LayoutParams(size, size));
           imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
           imageView.setAdjustViewBounds(true);
       } else {
           imageView = (ImageView) convertView;
       }

       Glide.with(context)
               .load(avatarIds[position])
               .circleCrop()
               .into(imageView);

       // Style dynamique pour la bordure
       imageView.setBackgroundResource(avatarIds[position] == selectedId
               ? R.drawable.avatar_selected_border
               : R.drawable.avatar_default_border);

       return imageView;
   }

    private int calculateCellSize(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int padding = (int) (16 * metrics.density);
        int spacing = (int) (20 * metrics.density);
        return (screenWidth - 2 * padding - 2 * spacing) / 3;
    }
}