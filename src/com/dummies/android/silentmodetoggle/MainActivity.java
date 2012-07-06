package com.dummies.android.silentmodetoggle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.SlidingDrawer;
import android.widget.Spinner;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */

	private AudioManager mAudioManager;
//	private boolean mPhoneIsSilent;
	Spinner spnHours;
	Timer timer;
	
	ImageView imageView;
	Drawable newPhoneImageOn;
	Drawable newPhoneImageOff;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Log.d("SilentModeApp", "iniciando");
        
        //Evento boton presionado
        setButtonClickListener();
        
        //Cargar icono
        initIcon();
        
        //Inicialización de servicio de audio
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        
        //Revisar estado toggle
        checkIfPhoneIsSilent();
        
        //Opciones de tiempo
        initHoursSpinner();
        
        //Mostrar hora actual
        getCurrentTime();
    }

    //Iniciar spinner de horas
    private void initHoursSpinner() {
    	spnHours = (Spinner) findViewById(R.id.spinHoras);
    	
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
    			this, R.array.opciones_horas, android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	spnHours.setAdapter(adapter);
    }
    
    //Conseguir hora actual del sistema
    private void getCurrentTime() {
    	EditText txtHoraActual = (EditText) findViewById(R.id.txtHoraActual);
    	txtHoraActual.setEnabled(false);
    	
    	Calendar calendario = Calendar.getInstance();
    	int haHoras = calendario.getTime().getHours();
    	int haMinutos = calendario.getTime().getMinutes();
    	
    	String horaActual = Integer.toString(haHoras)+":"+Integer.toString(haMinutos);
    	txtHoraActual.setText(horaActual);
    }
    
    //Evento de boton toggle
    private void setButtonClickListener() {
    	Button toggleButton = (Button) findViewById(R.id.toggleButton);
    	
    	//listener button click
        toggleButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//Cambiar a modo silencio
				mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				
				//Cambia icono
				toggleUiOff();
				
				//Espera para volver a modo normal
				String horasEspera = (String)spnHours.getSelectedItem();
				//setTestValue(horasEspera);
				int minutos = Integer.parseInt(horasEspera);
				
				timer = new Timer();
				timer.schedule
				(
						new TimerTask() {
							@Override
							public void run() {
								//regreso al modo normal
								excecuteTask();
							}
						}
						,
						//minutos*60000 <-- minutos
						minutos*1000 // <-- segundos
				);
			}
		});
    }
    
    //Init icon
    private void initIcon() {
    	imageView = (ImageView) findViewById(R.id.phone_icon);
    	
    	newPhoneImageOn = getResources().getDrawable(R.drawable.phoneon);
    	newPhoneImageOff = getResources().getDrawable(R.drawable.phoneoff);
    }
    
    //Tarea de cambio
    private void excecuteTask() {
    	Log.d("HERNAN", "Tiempo acabado!");
    	timer.cancel();
    	
    	final Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    	vib.vibrate(700);
    	vib.vibrate(700);
    	
		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);	
		mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
		
		finish();
    }
    
    //Ingresar valor de campo test
    private void setTestValue(String text) {
    	EditText txtTest = (EditText) findViewById(R.id.txtTstHoras);
    	
    	if(!text.equals(""))
    		txtTest.setText(text);
    	else
    		txtTest.setText("no llegan horas :(");
    }
    
    //Revisar si estado del telef esta en silencio para dejarlo en normal
    private void checkIfPhoneIsSilent() {
    	int ringerMode = mAudioManager.getRingerMode();
    	
    	if(ringerMode == AudioManager.RINGER_MODE_SILENT) {
    		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        	imageView.setImageDrawable(newPhoneImageOn);
    	}
    }
    
    //Cambia imagen UI de modo silencio a normal y vice versa
    private void toggleUiOff() {
    	imageView.setImageDrawable(newPhoneImageOff);
    }
    
    private void toggleUiOn() {
    	imageView.setImageDrawable(newPhoneImageOn);
    }
    
    protected void onResume() {
    	super.onResume();
//    	checkIfPhoneIsSilent();
//    	toggleUi();
    }
}