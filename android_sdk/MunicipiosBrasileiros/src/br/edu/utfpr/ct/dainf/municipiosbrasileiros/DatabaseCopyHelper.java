package br.edu.utfpr.ct.dainf.municipiosbrasileiros;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DatabaseCopyHelper extends SQLiteOpenHelper {

	private  static String DB_PATH = Environment.getDataDirectory()
			+ "/data/br.edu.utfpr.ct.dainf.municipiosbrasileiros/databases/";
	private static String DB_NAME = "municipios.db";
	private static int DB_VERSION = 1;
	private Context context;

	public DatabaseCopyHelper(Context c) {
		super(c, DB_NAME, null, DB_VERSION);
		context = c;
	}

	public void criaBaseDados() throws IOException {
		boolean dbexist = verificaBaseDados();
		if (!dbexist) {
			this.getReadableDatabase();
			this.close();
			try {
				copiaBaseDados();
			} catch (IOException e) {
				throw new Error("Erro copiando banco de	dados:" + e);
			}
		}
	}
	
	private void copiaBaseDados() throws IOException {
		InputStream arqEntrada = context.getAssets().open(DB_NAME);
		String nomeArqSaida = DB_PATH + DB_NAME;
		OutputStream arqSaida = new FileOutputStream(nomeArqSaida);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = arqEntrada.read(buffer)) > 0) {
			arqSaida.write(buffer, 0, length);
		}
		arqSaida.flush();
		arqSaida.close();
		arqEntrada.close();
	}
	
	private boolean verificaBaseDados() {
		File dbFile = new File(DB_PATH + DB_NAME);
		return dbFile.exists();
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}
	
	public static String getDbFilename() {
		return DB_PATH + DB_NAME;
	}

}
