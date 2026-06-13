package rs.fon.skolajezika.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import rs.fon.skolajezika.model.EvidencijaTermina;
import rs.fon.skolajezika.model.Jezik;
import rs.fon.skolajezika.model.Isplata;
import rs.fon.skolajezika.model.Kurs;
import rs.fon.skolajezika.model.Nivo;
import rs.fon.skolajezika.model.Profesor;
import rs.fon.skolajezika.model.StatusEvidencije;
import rs.fon.skolajezika.model.StatusIsplate;
import rs.fon.skolajezika.model.StatusTermina;
import rs.fon.skolajezika.model.StatusUpisa;
import rs.fon.skolajezika.model.StatusUplate;
import rs.fon.skolajezika.model.TerminCasa;
import rs.fon.skolajezika.model.TipKursa;
import rs.fon.skolajezika.model.Ucenik;
import rs.fon.skolajezika.model.Upis;
import rs.fon.skolajezika.model.Uplata;
import rs.fon.skolajezika.model.NastavnaGrupa;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public final class Responses {

    private Responses() {
    }

    public record UcenikResponse(Long id, String ime, String prezime, String kontakt, LocalDate datumUpisa) {
        public static UcenikResponse from(Ucenik ucenik) {
            return new UcenikResponse(ucenik.getId(), ucenik.getIme(), ucenik.getPrezime(), ucenik.getKontakt(), ucenik.getDatumUpisa());
        }
    }

    public record ProfesorResponse(Long id, String ime, String prezime, Set<Jezik> jezici, Set<Nivo> nivoi,
                                   String brojLicence, LocalDate datumAngazovanja, BigDecimal mesecnaNaknada) {
        public static ProfesorResponse from(Profesor profesor) {
            return new ProfesorResponse(profesor.getId(), profesor.getIme(), profesor.getPrezime(), profesor.getJezici(),
                    profesor.getNivoi(), profesor.getBrojLicence(), profesor.getDatumAngazovanja(), profesor.getMesecnaNaknada());
        }
    }

    public record KursResponse(Long id, Jezik jezik, Nivo nivo, TipKursa tip,
                               @JsonInclude(JsonInclude.Include.NON_NULL) BigDecimal cenaMesecno) {
        public static KursResponse from(Kurs kurs) {
            return new KursResponse(kurs.getId(), kurs.getJezik(), kurs.getNivo(), kurs.getTip(), kurs.getCenaMesecno());
        }
    }

    public record UpisResponse(Long id, Long ucenikId, Long kursId, Long grupaId, Long profesorId,
                               LocalDate datumPocetka, StatusUpisa status) {
        public static UpisResponse from(Upis upis) {
            return new UpisResponse(upis.getId(), upis.getUcenik().getId(), upis.getKurs().getId(),
                    upis.getGrupa() == null ? null : upis.getGrupa().getId(),
                    upis.getGrupa() == null ? null : upis.getGrupa().getProfesor().getId(),
                    upis.getDatumPocetka(), upis.getStatus());
        }
    }

    public record GrupaResponse(Long id, String naziv, Long profesorId, Long kursId, int kapacitet, long brojUcenika) {
        public static GrupaResponse from(NastavnaGrupa grupa, long brojUcenika) {
            return new GrupaResponse(grupa.getId(), grupa.getNaziv(), grupa.getProfesor().getId(),
                    grupa.getKurs().getId(), grupa.getKapacitet(), brojUcenika);
        }
    }

    public record TerminResponse(Long id, Long profesorId, Long kursId, Long grupaId, LocalDateTime vremeOd, LocalDateTime vremeDo,
                                 StatusTermina status, String zakazaoKorisnik, String otkazaoKorisnik,
                                 LocalDateTime vremeOtkazivanja) {
        public static TerminResponse from(TerminCasa termin) {
            return new TerminResponse(termin.getId(), termin.getProfesor().getId(), termin.getKurs().getId(),
                    termin.getGrupa() == null ? null : termin.getGrupa().getId(),
                    termin.getVremeOd(), termin.getVremeDo(), termin.getStatus(), termin.getZakazaoKorisnik(),
                    termin.getOtkazaoKorisnik(), termin.getVremeOtkazivanja());
        }
    }

    public record SedmicniPregledProfesoraResponse(Long profesorId, String profesor, long odrzaniCasovi,
                                                   long otkazaniCasovi, long zakazaniCasovi) {
    }

    public record EvidencijaTerminaResponse(Long id, Long upisId, Long terminCasaId, StatusEvidencije status, String napomena) {
        public static EvidencijaTerminaResponse from(EvidencijaTermina evidencija) {
            return new EvidencijaTerminaResponse(evidencija.getId(), evidencija.getUpis().getId(), evidencija.getTerminCasa().getId(), evidencija.getStatus(), evidencija.getNapomena());
        }
    }

    public record UplataResponse(Long id, Long ucenikId, Long kursId, int mesec, int godina,
                                 BigDecimal iznos, LocalDate datumUplate, StatusUplate status) {
        public static UplataResponse from(Uplata uplata) {
            return new UplataResponse(uplata.getId(), uplata.getUcenik().getId(), uplata.getKurs().getId(),
                    uplata.getMesec(), uplata.getGodina(), uplata.getIznos(), uplata.getDatumUplate(), uplata.getStatus());
        }
    }

    public record MesecnaObavezaResponse(
            Long upisId,
            Long kursId,
            Jezik jezik,
            Nivo nivo,
            int mesec,
            int godina,
            BigDecimal ocekivaniIznos,
            boolean placeno,
            Long uplataId,
            BigDecimal placeniIznos,
            LocalDate datumUplate
    ) {
    }

    public record IsplataResponse(Long id, Long profesorId, int mesec, int godina, BigDecimal iznos,
                                  LocalDate datumIsplate, StatusIsplate status) {
        public static IsplataResponse from(Isplata isplata) {
            return new IsplataResponse(isplata.getId(), isplata.getProfesor().getId(), isplata.getMesec(),
                    isplata.getGodina(), isplata.getIznos(), isplata.getDatumIsplate(), isplata.getStatus());
        }
    }

    public record MesecnaIsplataResponse(
            Long profesorId,
            int mesec,
            int godina,
            boolean isplaceno,
            Long isplataId,
            BigDecimal isplaceniIznos,
            LocalDate datumIsplate
    ) {
    }

    public record StatistikaResponse(
            long brojUcenika,
            long brojProfesora,
            long brojKurseva,
            long brojUpisa,
            long brojTermina,
            long brojUplata
    ) {
    }
}
