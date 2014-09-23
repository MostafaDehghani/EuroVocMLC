package nl.uva.mlc.eurovoc;

import java.util.ArrayList;
import java.util.Objects;


/**
 *
 * @author  Mostafa Dehghani
 */
public class EuroVocDoc {
    
    private String id;
    private String n;
    private String lang;
    private String creationDate;
    private String title;
    private String url;
    private String note;
    private String text;
    private String namedEntities;
    private ArrayList<String> classes;

    public EuroVocDoc(String id, String title, String text, String namedEntities, ArrayList<String> classes) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.namedEntities = namedEntities;
        this.classes = classes ;
    }
    
    public EuroVocDoc(String id, String n, String lang, String creationDate, String title, String url, String note, String text, ArrayList<String> classes) {
        this.id = id;
        this.n = n;
        this.lang = lang;
        this.creationDate = creationDate;
        this.title = title;
        this.url = url;
        this.note = note;
        this.text = text;
        this.classes = classes ;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setN(String n) {
        this.n = n;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setNamedEntities(String namedEntities) {
        this.namedEntities = namedEntities;
    }

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }
    
    
    public String getNamedEntities() {
        return namedEntities;
    }

    public String getId() {
        return id;
    }

    public String getN() {
        return n;
    }

    public String getLang() {
        return lang;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getNote() {
        return note;
    }

    public String getText() {
        return text;
    }
    public ArrayList<String>  getClasses() {
        return classes;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EuroVocDoc other = (EuroVocDoc) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }   
}
