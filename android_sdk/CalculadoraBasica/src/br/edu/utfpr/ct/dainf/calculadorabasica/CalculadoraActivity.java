package br.edu.utfpr.ct.dainf.calculadorabasica;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

public class CalculadoraActivity extends Activity {
	
	EditText display;
	TextView mensagem;
	
	double ultimo_operando = Double.NaN;
	char operador = ' ';
	
	boolean clear = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_calculadora);
        
        display = (EditText) findViewById(R.id.editText1);
        mensagem = (TextView) findViewById(R.id.textView1);
        
    }
    
    
    public void cancelaOperacao(View v) {
    	ultimo_operando = Double.NaN;
    	operador = ' ';
    	display.setText("");
    	mensagem.setText("");
    	clear = true;
    }
    
    public void operadorClicado(View v) {
    	int id = v.getId();
    	double res;
    	double operando_atual;
    	String texto = display.getText().toString();
    	
    	if(texto.isEmpty())
    		return;
    	else
    		operando_atual = Double.valueOf(texto);
    	
    	switch(id) {
	    	case R.id.button12: // botao '='
	    		break;
	    	case R.id.button13: // botao '/'
	    		operador = '/';
	    		break;
	    	case R.id.button14: // botao '*'
	    		operador = '*';
	    		break;
	    	case R.id.button15: // botao '-'
	    		operador = '-';
	    		break;
	    	case R.id.button16: // botao '+'
	    		operador = '+';
	    		break;
    	}

		if(!Double.isNaN(ultimo_operando)) {
			res = calcula(ultimo_operando, operando_atual, operador);
			if(Double.isNaN(res)) {
				mensagem.setText(getString(R.string.msg_div0));
			}
			display.setText(String.valueOf(res));
	        display.setSelection(display.length());
			if(id == R.id.button12) {
				ultimo_operando = Double.NaN;
				operador = ' ';
			} else {
				ultimo_operando = res;
			}
		} else {
			display.setText("");
	    	ultimo_operando = operando_atual;
		}
        clear = true;
    }
    
    public void numeroClicado(View v) {
    	Button botao = (Button) v;
    	int start = display.getSelectionStart();
    	int end = display.getSelectionEnd();
    	if(clear) {
    		display.setText(botao.getText());
	        display.setSelection(display.length());
    		clear = false;
    	} else {
    		display.getText().replace(start, end, botao.getText());
    		if(start+1 < display.length())
    			display.setSelection(start+1);
    	}
    }

    private double calcula(double v1, double v2, char op) {
    	double res;
    	switch(op) {
    	case '+':
    		res = v1 + v2;
    		break;
    	case '-':
    		res = v1 - v2;
    		break;
    	case '*':
    		res = v1 * v2;
    		break;
    	case '/':
    		res = v1 / v2;
    		break;
    	default:
    		res = v2;
    	}
    	return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calculadora, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_calculadora, container, false);
            return rootView;
        }
    }

}
