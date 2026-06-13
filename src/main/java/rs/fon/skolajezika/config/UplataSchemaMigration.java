package rs.fon.skolajezika.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UplataSchemaMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public UplataSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        if (!postojiKolona("UPLATA", "UPIS_ID")) {
            postaviObavezneVeze();
            return;
        }

        jdbcTemplate.update("""
                UPDATE UPLATA
                SET UCENIK_ID = (SELECT UCENIK_ID FROM UPIS WHERE UPIS.ID = UPLATA.UPIS_ID),
                    KURS_ID = (SELECT KURS_ID FROM UPIS WHERE UPIS.ID = UPLATA.UPIS_ID)
                WHERE UPIS_ID IS NOT NULL
                """);

        List<String> ogranicenja = jdbcTemplate.queryForList("""
                SELECT DISTINCT KCU.CONSTRAINT_NAME
                FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE KCU
                WHERE KCU.TABLE_NAME = 'UPLATA' AND KCU.COLUMN_NAME = 'UPIS_ID'
                """, String.class);
        for (String ogranicenje : ogranicenja) {
            jdbcTemplate.execute("ALTER TABLE UPLATA DROP CONSTRAINT " + ogranicenje);
        }
        jdbcTemplate.execute("ALTER TABLE UPLATA DROP COLUMN UPIS_ID");
        postaviObavezneVeze();
    }

    private void postaviObavezneVeze() {
        if (postojiKolona("UPLATA", "UCENIK_ID")) {
            jdbcTemplate.execute("ALTER TABLE UPLATA ALTER COLUMN UCENIK_ID SET NOT NULL");
        }
        if (postojiKolona("UPLATA", "KURS_ID")) {
            jdbcTemplate.execute("ALTER TABLE UPLATA ALTER COLUMN KURS_ID SET NOT NULL");
        }
    }

    private boolean postojiKolona(String tabela, String kolona) {
        Integer broj = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_NAME = ? AND COLUMN_NAME = ?
                """, Integer.class, tabela, kolona);
        return broj != null && broj > 0;
    }
}
