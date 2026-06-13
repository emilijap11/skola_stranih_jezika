package rs.fon.skolajezika.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class KorisnickiNalog extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String korisnickoIme;

    @NotBlank
    @Column(nullable = false)
    private String lozinkaHash;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private UlogaNaloga uloga;

    @Column(nullable = false)
    private boolean moraPromenitiLozinku;

    @OneToOne(fetch = FetchType.EAGER)
    private Ucenik ucenik;

    @OneToOne(fetch = FetchType.EAGER)
    private Profesor profesor;

    protected KorisnickiNalog() {
    }

    public KorisnickiNalog(String korisnickoIme, String lozinkaHash, UlogaNaloga uloga,
                           boolean moraPromenitiLozinku, Ucenik ucenik, Profesor profesor) {
        this.korisnickoIme = korisnickoIme;
        this.lozinkaHash = lozinkaHash;
        this.uloga = uloga;
        this.moraPromenitiLozinku = moraPromenitiLozinku;
        this.ucenik = ucenik;
        this.profesor = profesor;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public String getLozinkaHash() {
        return lozinkaHash;
    }

    public UlogaNaloga getUloga() {
        return uloga;
    }

    public boolean isMoraPromenitiLozinku() {
        return moraPromenitiLozinku;
    }

    public Ucenik getUcenik() {
        return ucenik;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void promeniLozinku(String noviHash) {
        this.lozinkaHash = noviHash;
        this.moraPromenitiLozinku = false;
    }
}
