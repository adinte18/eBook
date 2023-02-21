package sample;

public class Commentaire {

    private String citat;
    private String comm;

    public Commentaire(String comm, String citat)
    {
        this.citat = citat;
        this.comm = comm;
    }

    public String getCitat() {
        return citat;
    }

    public String getComm() {
        return comm;
    }
}
