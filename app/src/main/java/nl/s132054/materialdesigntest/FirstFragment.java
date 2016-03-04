package nl.s132054.materialdesigntest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FirstFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FirstFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    class BitmapCache {
        Bitmap cached;
    }

    class RecyclerCardAdapter extends RecyclerView.Adapter<RecyclerCardAdapter.ViewHolder> {

        List<RecyclerCardAdapter.ViewHolder> cards = new ArrayList<>();
        private ArrayList<String> items;
        private HashMap<Integer, BitmapCache> cache;

        RecyclerCardAdapter(String[] items) {
            this.items = new ArrayList<String>();
            Collections.addAll(this.items, items);
            cache = new HashMap<>();
        }

        @Override
        public RecyclerCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_card, parent, false);
            ImageView imageView = (ImageView) cardView.getChildAt(0);
            TextView textView = (TextView) cardView.getChildAt(1);
            return new ViewHolder(cardView, textView, imageView);
        }

        @Override
        public void onBindViewHolder(RecyclerCardAdapter.ViewHolder holder, int position) {
            holder.text.setText(items.get(position));
            if (!cache.containsKey(position)) {
                BitmapCache bitmapCache = new BitmapCache();
                cache.put(position, bitmapCache);
                ImageTask imageTask = new ImageTask(holder.image, bitmapCache);
                imageTask.execute(items.get(position));
            } else {
                holder.image.setImageBitmap(cache.get(position).cached);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void addItem(String item) {
            items.add(item);
            notifyItemChanged(getItemCount() - 1);

        }

        public void removeLastItem() {
            items.remove(items.size() - 1);
            notifyItemChanged(getItemCount());
        }

        class ImageTask extends AsyncTask<String, Void, Bitmap> {

            ImageView imageView;
            BitmapCache bitmapCache;

            ImageTask(ImageView img, BitmapCache cache) {
                imageView = img;
                bitmapCache = cache;
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String url = params[0];
                Bitmap result = null;
                try {
                    InputStream in = new URL(url).openStream();
                    result = BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                bitmapCache.cached = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                if (bitmap == null) {
                    imageView.setImageResource(R.drawable.notfound);
                } else {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            CardView card;
            TextView text;
            ImageView image;

            public ViewHolder(CardView itemView, TextView text, ImageView image) {
                super(itemView);
                this.card = itemView;
                this.text = text;
                this.image = image;
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) this.getActivity().findViewById(R.id.layout_content_cards);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        String[] items = new String[]{"http://s4.evcdn.com/images/block250/I0-001/016/304/559-0.jpeg_/massive-attack-59.jpeg",
                "http://s3.evcdn.com/images/block250/I0-001/000/273/446-5.jpg_/muse-46.jpg",
                "http://s3.evcdn.com/images/block250/I0-001/004/242/482-9.jpeg_/adele-82.jpeg"};

        final RecyclerCardAdapter recyclerCardAdapter = new RecyclerCardAdapter(items);
        recyclerView.setAdapter(recyclerCardAdapter);

        FloatingActionButton fab = (FloatingActionButton) this.getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerCardAdapter.addItem("http://dummyimage.com/600x400/ffffff/aaaaaa.png&text=" + String.valueOf(recyclerCardAdapter.getItemCount()));
                Snackbar.make(view, "Item added", Snackbar.LENGTH_LONG)
                        .setAction("undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                recyclerCardAdapter.removeLastItem();
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
