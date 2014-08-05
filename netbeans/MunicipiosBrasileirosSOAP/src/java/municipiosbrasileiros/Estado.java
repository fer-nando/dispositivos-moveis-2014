
package municipiosbrasileiros;

/**
 *
 * @author fernando
 */
public class Estado {

  private int id;
  private String nome;
  private String uf;  
  
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getUf() {
    return uf;
  }

  public void setUf(String uf) {
    this.uf = uf;
  }
  
  @Override
	public String toString() {
		return "" + uf + ": " + nome;
  }
  
}
