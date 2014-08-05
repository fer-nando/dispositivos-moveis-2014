package br.edu.utfpr.ct.dainf.calculadoraipv4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	AlertDialog alert;	
	Spinner subnet_spinner, netmask_spinner;
	ArrayAdapter<CharSequence> subnet_adapter, netmask_adapter;
	EditText[] ip_fields = new EditText[4];
	
	char ip_class = 'A';
	int[] ip_addr = new int[4];
	int[] netmask = new int[4];
	int[] wildcard = new int[4];
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		
		subnet_spinner = (Spinner) findViewById(R.id.spinner1);
		netmask_spinner = (Spinner) findViewById(R.id.spinner2);
		// Criar ArrayAdapter
		subnet_adapter = ArrayAdapter.createFromResource(this, 
				R.array.subnet_classA_array,
				android.R.layout.simple_spinner_dropdown_item);
		netmask_adapter = ArrayAdapter.createFromResource(this, 
				R.array.netmask_classA_array,
				android.R.layout.simple_spinner_dropdown_item);
		// Especificar layout da lista de opcoes
		subnet_adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		netmask_adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		// Aplicar adapter
		subnet_spinner.setAdapter(subnet_adapter);
		netmask_spinner.setAdapter(netmask_adapter);
		
		ip_fields[0] = (EditText) findViewById(R.id.editText1);
		ip_fields[1] = (EditText) findViewById(R.id.editText2);
		ip_fields[2] = (EditText) findViewById(R.id.editText3);
		ip_fields[3] = (EditText) findViewById(R.id.editText4);
		
		ip_fields[0].addTextChangedListener(new IPTextWatcher(this));
		
		BitsSpinnerListener spinner_listener = new BitsSpinnerListener();
		subnet_spinner.setOnItemSelectedListener(spinner_listener);
		netmask_spinner.setOnItemSelectedListener(spinner_listener);

	}
	
	public void calculaSubredes(View v) {
		
		int i;
		int[] rede = new int[4];
		int[] bc = new int[4];
		int bits_mascara = 0;
		int bits_subrede, num_subredes;
		int num_hosts;
		TextView tv_rede 		= (TextView) findViewById(R.id.tvRede);
		TextView tv_mascara 	= (TextView) findViewById(R.id.tvMascara);
		TextView tv_bc 			= (TextView) findViewById(R.id.tvBroadcast);
		TextView tv_hmin 		= (TextView) findViewById(R.id.tvHostMin);
		TextView tv_hmax 		= (TextView) findViewById(R.id.tvHostMax);
		TextView tv_subredes 	= (TextView) findViewById(R.id.tvSubredes);
		TextView tv_hosts 		= (TextView) findViewById(R.id.tvHosts);
		
		try {

			bits_subrede = Integer.valueOf(
					subnet_spinner.getSelectedItem().toString());
			
			for(i=0; i < 4; i++) {
				String s1 = ip_fields[i].getText().toString();				
				if(s1.isEmpty())
					throw new Exception(
							"Endereço IPv4;Os campos não podem estar vazios.");				
				ip_addr[i] = Integer.valueOf(s1);
				if(ip_addr[i] >= 0 && ip_addr[i] <= 255) {
					ip_fields[i].setTextColor(Color.BLACK);
				} else {
					ip_fields[i].setTextColor(Color.RED);
					throw new Exception("Endereço IPv4;Valor inválido!");
				}
			}
			
			// classe
			if(ip_addr[0] < 128) {
				ip_class = 'A';
				bits_mascara = 8 + bits_subrede;
			} else if(ip_addr[0] < 192) {
				ip_class = 'B';
				bits_mascara = 16 + bits_subrede;
			} else {
				ip_class = 'C';
				bits_mascara = 24 + bits_subrede;
			}
			
			// mascara
			int bits_rem = bits_mascara;
			for(i=0; i<4; i++) {
				if(bits_rem >= 8)
					netmask[i] = 255;
				else if(bits_rem > 0)
					netmask[i] = 255 - (255 >> bits_rem);
				else
					netmask[i] = 0;
				bits_rem -= 8;
				wildcard[i] = 255 - netmask[i];
			}
			tv_mascara.setText(IP_arrayParaString(netmask));
			
			// rede
			String string_rede;
				
			for(i=0; i<4; i++) {
				rede[i] = ip_addr[i] & netmask[i];
			}
			string_rede = IP_arrayParaString(rede) 
					+ "/" + String.valueOf(bits_mascara) 
					+ " (Classe " + ip_class +  ")";
			tv_rede.setText(string_rede);
			
			//broadcast
			String string_bc;
			for(i=0; i < 4; i++) {
				bc[i] = rede[i] + wildcard[i];
			}
			string_bc = IP_arrayParaString(bc);
			tv_bc.setText(string_bc);
			
			// host min
			rede[3] += 1;
			tv_hmin.setText(IP_arrayParaString(rede));

			// host max
			bc[3] -= 1;
			tv_hmax.setText(IP_arrayParaString(bc));
			
			// subredes
			num_subredes = ((int)Math.pow(2.0, bits_subrede));
			tv_subredes.setText(String.valueOf(num_subredes));

			// hosts
			num_hosts = ((int)Math.pow(2.0, 32.0-bits_mascara)) - 2;
			tv_hosts.setText(String.valueOf(num_hosts));
			
		} catch(Exception ex) {
			String[] tokens = ex.getMessage().split(";");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(tokens[0]);
			builder.setMessage(tokens[1]);
			builder.setPositiveButton("OK", null);
			alert = builder.create();
			alert.show();
		}
		
	}
	
	private String IP_arrayParaString(int[] campos) {
		String s = String.valueOf(campos[0]) 
				+ "." + String.valueOf(campos[1])
				+ "." + String.valueOf(campos[2])
				+ "." + String.valueOf(campos[3]);
		return s;
	}
	
	
	class IPTextWatcher implements TextWatcher {
		
		Context context;
		
		public IPTextWatcher(Context c) {context = c;}
		@Override
		public void afterTextChanged(Editable s) {
			if(s.length() > 0) {
				int new_selection;
				int first_octet = Integer.valueOf(s.toString());
				CharSequence last_subrede_selection = 
						(CharSequence) subnet_spinner.getSelectedItem();
				
				if(first_octet < 128) {
					if(ip_class != 'A') {
						ip_class = 'A';
						subnet_adapter = ArrayAdapter.createFromResource(context, 
								R.array.subnet_classA_array,
								android.R.layout.simple_spinner_dropdown_item);
						netmask_adapter = ArrayAdapter.createFromResource(context, 
								R.array.netmask_classA_array,
								android.R.layout.simple_spinner_dropdown_item);
					}
				} else if(first_octet < 192) {
					if(ip_class != 'B') {
						ip_class = 'B';
						subnet_adapter = ArrayAdapter.createFromResource(context, 
								R.array.subnet_classB_array,
								android.R.layout.simple_spinner_dropdown_item);
						netmask_adapter = ArrayAdapter.createFromResource(context, 
								R.array.netmask_classB_array,
								android.R.layout.simple_spinner_dropdown_item);
					}
				} else if(ip_class != 'C') {
					ip_class = 'C';
					subnet_adapter = ArrayAdapter.createFromResource(context, 
							R.array.subnet_classC_array,
							android.R.layout.simple_spinner_dropdown_item);
					netmask_adapter = ArrayAdapter.createFromResource(context, 
							R.array.netmask_classC_array,
							android.R.layout.simple_spinner_dropdown_item);
				}

				subnet_spinner.setAdapter(subnet_adapter);
				netmask_spinner.setAdapter(netmask_adapter);
				
				new_selection = subnet_adapter.getPosition(
						last_subrede_selection);
				if(new_selection < 0)
					new_selection = 0;
				
				subnet_spinner.setSelection(new_selection);
				netmask_spinner.setSelection(new_selection);
			}
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, 
				int arg3) {}
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, 
				int arg3) {}
	}
	
	class BitsSpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, 
				int position, long id) {
			if((Spinner) parent == subnet_spinner) {
				netmask_spinner.setSelection(position);
			} else if((Spinner) parent == netmask_spinner) {
				subnet_spinner.setSelection(position);
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
		
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
