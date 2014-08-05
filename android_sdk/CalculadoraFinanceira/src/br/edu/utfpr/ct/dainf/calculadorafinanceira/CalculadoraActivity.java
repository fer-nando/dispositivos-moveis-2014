package br.edu.utfpr.ct.dainf.calculadorafinanceira;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CalculadoraActivity extends Activity {
	
	EditText display;
	
	double ultimo_operando = Double.NaN;
	char operador = ' ';
	
	boolean clear = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculadora);
        
        display = (EditText) findViewById(R.id.editText1);
        
        ((TextView)findViewById(R.id.button37)).setText(Html.fromHtml("y<sup>x</sup>"));
        
    }
    
    
    public void cancelaOperacao(View v) {
    	ultimo_operando = Double.NaN;
    	operador = ' ';
    	display.setText("");
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
	    	case R.id.button18: // botao 'Enter'
				operador = ' ';
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
	    	default: operador = ' ';
    	}
    	
    	if(id == R.id.button18) {
    		display.setText("");
	    	ultimo_operando = operando_atual;
    	} else {
    		if(!Double.isNaN(ultimo_operando)) {
    			res = calcula(ultimo_operando, operando_atual, operador);
    			display.setText(String.valueOf(res));
    	        display.setSelection(display.length());
    	        ultimo_operando = res;
    		} else {
    			display.setText(getString(R.string.msg_invop));
    		}
    	}

		
        clear = true;
    }
    
    public void numeroClicado(View v) {
    	Button botao = (Button) v;
    	int start = display.getSelectionStart();
    	int end = display.getSelectionEnd();
    	String bText = botao.getText().toString();
    	if(clear && !bText.equals("CHS")) {
    		display.setText(bText);
	        display.setSelection(display.length());
    		clear = false;
    	} else {
    		if(!bText.equals("CHS")) {
	    		display.getText().replace(start, end, botao.getText());
	    		if(start+1 < display.length())
	    			display.setSelection(start+1);
    		} else {
    			Double num = -Double.valueOf(display.getText().toString());
    			display.setText(num.toString());
    		}
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
}
