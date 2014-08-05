package br.edu.utfpr.ct.dainf.conversordemedidas;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity {

	RadioGroup opcoes;
	RadioButton opcao1, opcao2;
	TextView texto1, texto2;
	EditText campo1, campo2;
	double [] fator_conversao = {
			3.2808399,	// metros <-> pes
			0.621371192 // km     <-> milhas
			};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        opcoes = (RadioGroup) findViewById(R.id.radioGroup1);
        opcao1 = (RadioButton) findViewById(R.id.radio0);
        opcao2 = (RadioButton) findViewById(R.id.radio1);
        texto1 = (TextView) findViewById(R.id.textView2);
        texto2 = (TextView) findViewById(R.id.textView4);
        campo1 = (EditText) findViewById(R.id.editText1);
        campo2 = (EditText) findViewById(R.id.editText2);
        
        campo1.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {
				if (campo1.isFocused()) {
					int opcao;
					if(opcoes.getCheckedRadioButtonId() == opcao1.getId()) {
						opcao = 0;
					} else {
						opcao = 1;
					}
					//try {
						double v1 = Double.valueOf(s.toString());
						double v2 = v1 * fator_conversao[opcao];
						campo2.setText(String.valueOf(v2));
					//	campo1.setTextColor(Color.BLACK);
					//	campo1.setHint("");
					//} catch (Exception ex) {
					//	campo1.setTextColor(Color.RED);
					//	campo1.setHint("Valor inválido");
					//}
				}
			}
		});
        
        campo2.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {
				if (campo2.isFocused()) {
					int opcao;
					if(opcoes.getCheckedRadioButtonId() == opcao1.getId()) {
						opcao = 0;
					} else {
						opcao = 1;
					}
					//try {
						double v2 = Double.valueOf(s.toString());
						double v1 = v2 / fator_conversao[opcao];
						campo1.setText(String.valueOf(v1));
					//} catch (Exception ex) {
					//	System.err.println("Valor inválido");
					//}
				}
			}
		});
        
        opcoes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				String s1, s2;
				if(checkedId == opcao1.getId()) {
					s1 = getResources().getString(R.string.abrev_metros);
					s2 = getResources().getString(R.string.abrev_pes);
				} else {
					s1 = getResources().getString(R.string.abrev_km);
					s2 = getResources().getString(R.string.abrev_milhas);
				}
				texto1.setText(s1);
				texto2.setText(s2);
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
