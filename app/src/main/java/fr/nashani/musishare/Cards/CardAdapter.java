package fr.nashani.musishare.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import fr.nashani.musishare.R;

/**
 * The type Card adapter.
 */
public class CardAdapter extends ArrayAdapter<Card> {

    /**
     * Instantiates a new Card adapter.
     *
     * @param context the context
     * @param items   the items
     */
    public CardAdapter(Context context ,  List<Card> items) {
        super(context , 0 , items);
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        Card cardItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent , false);
        }

        TextView name = convertView.findViewById(R.id.userName);
        ImageView image = convertView.findViewById(R.id.userImage);
        ImageView imageAlbum = convertView.findViewById(R.id.imageAlbum);
        TextView trackName = convertView.findViewById(R.id.name);
        TextView TrackArtist = convertView.findViewById(R.id.trackArtist);
        TextView TrackAlbum = convertView.findViewById(R.id.trackAlbum);
        TextView address = convertView.findViewById(R.id.address);

        name.setText(cardItem.getName());
        trackName.setText(cardItem.getTrackName());
        TrackArtist.setText(cardItem.getTrackArtist());
        TrackAlbum.setText(cardItem.getTrackAlbum());
        address.setText(cardItem.getAddress());

        /*
          Utilisation d'une librairie Glide pour peupler une imageView
         */
        Glide.with(convertView.getContext()).load(cardItem.getTrackAlbumCover()).into(imageAlbum);

        switch (cardItem.getProfileImageUrl()){
            case "default" :
                image.setImageResource(R.drawable.music_note);
            break;

            default:
                Glide.with(convertView.getContext()).load(cardItem.getProfileImageUrl()).into(image);
            break;

        }
        return convertView;
    }

}
