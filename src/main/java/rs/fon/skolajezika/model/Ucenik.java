package rs.fon.skolajezika.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class Ucenik extends BaseEntity {

    @NotBlank
    private String ime;

    @NotBlank
    private String prezime;

    @NotBlank
    private String kontakt;

    @NotNull
    private LocalDate datumUpisa;

    protected Ucenik() {
    }

    public Ucenik(String ime, String prezime, String kontakt, LocalDate datumUpisa) {
        uredi(ime, prezime, kontakt, datumUpisa);
    }

    public void uredi(String ime, String prezime, String kontakt, LocalDate datumUpisa) {
        this.ime = ime;
        this.prezime = prezime;
        this.kontakt = kontakt;
        this.datumUpisa = datumUpisa;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getKontakt() {
        return kontakt;
    }

    public void setKontakt(String kontakt) {
        this.kontakt = kontakt;
    }

    public void promeniKontakt(String kontakt) {
        this.kontakt = kontakt;
    }

    public LocalDate getDatumUpisa() {
        return datumUpisa;
    }

    public void setDatumUpisa(LocalDate datumUpisa) {
        this.datumUpisa = datumUpisa;
    }
}
