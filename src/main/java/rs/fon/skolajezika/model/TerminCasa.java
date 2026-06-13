package rs.fon.skolajezika.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class TerminCasa extends BaseEntity {

    @NotNull
    private LocalDateTime vremeOd;

    @NotNull
    private LocalDateTime vremeDo;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusTermina status;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Profesor profesor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Kurs kurs;

    @ManyToOne(fetch = FetchType.EAGER)
    private NastavnaGrupa grupa;

    private String zakazaoKorisnik;

    private String otkazaoKorisnik;

    private LocalDateTime vremeOtkazivanja;

    protected TerminCasa() {
    }

    public TerminCasa(Profesor profesor, Kurs kurs, LocalDateTime vremeOd, LocalDateTime vremeDo) {
        this(profesor, kurs, vremeOd, vremeDo, "sistem");
    }

    public TerminCasa(Profesor profesor, Kurs kurs, LocalDateTime vremeOd, LocalDateTime vremeDo, String zakazaoKorisnik) {
        uredi(profesor, kurs, vremeOd, vremeDo);
        this.status = StatusTermina.ZAKAZAN;
        this.zakazaoKorisnik = zakazaoKorisnik;
    }

    public void uredi(Profesor profesor, Kurs kurs, LocalDateTime vremeOd, LocalDateTime vremeDo) {
        this.profesor = profesor;
        this.kurs = kurs;
        this.vremeOd = vremeOd;
        this.vremeDo = vremeDo;
    }

    public LocalDateTime getVremeOd() {
        return vremeOd;
    }

    public LocalDateTime getVremeDo() {
        return vremeDo;
    }

    public StatusTermina getStatus() {
        return status;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public Kurs getKurs() {
        return kurs;
    }

    public NastavnaGrupa getGrupa() { return grupa; }

    public void dodeliGrupu(NastavnaGrupa grupa) {
        this.grupa = grupa;
        this.profesor = grupa.getProfesor();
        this.kurs = grupa.getKurs();
    }

    public String getZakazaoKorisnik() {
        return zakazaoKorisnik == null ? "nepoznato" : zakazaoKorisnik;
    }

    public String getOtkazaoKorisnik() {
        return otkazaoKorisnik;
    }

    public LocalDateTime getVremeOtkazivanja() {
        return vremeOtkazivanja;
    }

    public void otkazi(String korisnickoIme) {
        this.status = StatusTermina.OTKAZAN;
        this.otkazaoKorisnik = korisnickoIme;
        this.vremeOtkazivanja = LocalDateTime.now();
    }

    public void otkazi() {
        otkazi("sistem");
    }

    public void oznaciKaoOdrzan() {
        this.status = StatusTermina.ODRZAN;
    }
}
