package foureyes.com.histriasparadormir.Model;

/**
 * Created by dev on 21/02/18.
 */

public class Historia {
    private String titulo;
    private String conteudo;
    private String tipo;
    private String thumb;

    public Historia(String titulo, String conteudo, String tipo, String thumb) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.tipo = tipo;
        this.thumb = thumb;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
