package rs.fon.skolajezika.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class NastavnaGrupa extends BaseEntity {

    @NotBlank
    private String naziv;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @NotNull
    private Profesor profesor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @NotNull
    private Kurs kurs;

    protected NastavnaGrupa() {
    }

    public NastavnaGrupa(String naziv, Profesor profesor, Kurs kurs) {
        uredi(naziv, profesor, kurs);
    }

    public void uredi(String naziv, Profesor profesor, Kurs kurs) {
        this.naziv = naziv.trim();
        this.profesor = profesor;
        this.kurs = kurs;
    }

    public String getNaziv() { return naziv; }
    public Profesor getProfesor() { return profesor; }
    public Kurs getKurs() { return kurs; }
    public int getKapacitet() { return kurs.getTip() == TipKursa.GRUPNI ? 5 : 1; }
}
