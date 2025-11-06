//https://github.com/Chandrabhanaher/Genertae_qr_code/blob/master/README.md
package com.example.deimos_events.ui.createEvent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.deimos_events.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.Organizer;
import com.example.deimos_events.R;
import com.example.deimos_events.SessionManager;
import com.example.deimos_events.UserInterfaceManager;
import com.example.deimos_events.databinding.FragmentCreateEventBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class createFragment extends Fragment {
    private FragmentCreateEventBinding binding;

    private createViewModel viewModel;
    private Button upload;
    private ImageView image;
    private EditText title;
    private EditText Description;
    private EditText month;
    private EditText day;
    private EditText cap;
    private TextView desc;
    private TextView registration;
    private TextView capped;
    private TextView open;
    private TextView geo;
    private Switch location;
    private Button create;
    private EditText year;





    @Nullable
    @Override
     public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_create_event, container,false);

        upload = view.findViewById(R.id.button);
        title = view.findViewById(R.id.title);
        Description = view.findViewById(R.id.editText);
        month = view.findViewById(R.id.month);
        day = view.findViewById(R.id.day);
        cap = view.findViewById(R.id.cap);
        desc = view.findViewById(R.id.textView);
        registration = view.findViewById(R.id.textView3);
        capped = view.findViewById(R.id.textView4);
        open = view.findViewById(R.id.textView5);
        geo = view.findViewById(R.id.textView6);
        location = view.findViewById(R.id.switch1);
        year = view.findViewById(R.id.year);
        create = view.findViewById(R.id.create);
        image = view.findViewById(R.id.imageView);
        final ActivityResultLauncher<String> pickImageLauncher =
                registerForActivityResult(new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null) {
                                image.setImageURI(uri);
                            }
                        });
        viewModel = new ViewModelProvider(this).get(createViewModel.class);
        upload.setOnClickListener(v ->{
            pickImageLauncher.launch("image/*");
        });

        create.setOnClickListener(v->{
            String name = title.getText().toString();
            String d = day.getText().toString();
            String m = month.getText().toString();
            Integer capacity = Integer.parseInt(cap.getText().toString());
            Boolean loc = location.isChecked();
            String y = year.getText().toString();
            String decs = Description.getText().toString();
            SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
            String dateString = d +" "+ m+" "+ y;
            Date date;
            try {
                date =  formatter.parse(dateString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Bitmap imageBit = ((BitmapDrawable)image.getDrawable()).getBitmap();
            UUID id = UUID.randomUUID();
            String uniqueId = id.toString();
            BitMatrix qr;
            try {
                //will come back to add the fragment after it has been created
               qr = new MultiFormatWriter().encode(uniqueId,BarcodeFormat.QR_CODE, 400,400);
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
            SessionManager SM = ((EventsApp)requireActivity().getApplicationContext()).getSessionManager();
            EventManager EM = new EventManager(SM);

            EM.createEvent(uniqueId,name,imageBit,decs,date,capacity,loc,qr);

            NavController navController = NavHostFragment.findNavController(this);
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.navigation_events, true).build();
            Bundle arg = new Bundle();
            arg.putString("id", uniqueId);
            navController.navigate(R.id.navigation_edit, arg, navOptions);

        });

    return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
