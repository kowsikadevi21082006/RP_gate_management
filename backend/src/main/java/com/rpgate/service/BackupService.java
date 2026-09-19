package com.rpgate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgate.dto.BackupDtos;
import com.rpgate.dto.MovementDtos;
import com.rpgate.dto.PersonnelDtos;
import com.rpgate.model.Category;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import java.io.*;
import java.nio.file.*;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.*;

@Service
public class BackupService {
    private static final List<String> REQUIRED_TABLES = List.of("personnel", "movement_records", "leave_records", "td_posting_records", "vehicle_records", "outpass_records", "night_pass_records");
    private final PersonnelService personnel; private final MovementService movements; private final ObjectMapper mapper; private final JdbcTemplate db; private final Path databasePath;
    public BackupService(PersonnelService personnel, MovementService movements, ObjectMapper mapper, JdbcTemplate db, @Value("${rp.gate.database-path:${user.dir}/../database/rp_gate.db}") String databasePath) { this.personnel=personnel; this.movements=movements; this.mapper=mapper; this.db=db; this.databasePath=Path.of(databasePath); }
    public BackupDtos.Backup exportData() { return new BackupDtos.Backup("1", Instant.now().toString(), personnel.list(null), movements.list(new MovementDtos.Filter(null,null,null,null,null,null,null))); }
    public byte[] exportDatabase() { Path temporary=null; try { Files.createDirectories(databasePath.toAbsolutePath().getParent()); temporary=Files.createTempFile("rp-gate-backup-", ".db"); Files.deleteIfExists(temporary); db.execute("VACUUM INTO '"+sqlPath(temporary)+"'"); validateDatabase(temporary); return Files.readAllBytes(temporary); } catch (IOException e) { throw new IllegalArgumentException("Unable to create the SQLite backup file"); } finally { deleteQuietly(temporary); } }
    public BackupDtos.RestoreResult restoreJson(InputStream input) {
        try {
            BackupDtos.Backup backup=mapper.readValue(input, BackupDtos.Backup.class);
            if (backup == null || backup.personnel() == null || backup.movements() == null) throw new IllegalArgumentException("Backup must contain personnel and movements arrays");
            int people=0, records=0;
            for (PersonnelDtos.Response p : backup.personnel()) { if (p.name()==null || p.name().isBlank()) throw new IllegalArgumentException("Backup contains personnel without a name"); personnel.create(new PersonnelDtos.Request(p.armyNo(),p.cardNo(),p.rank(),p.name(),p.mobileNo(),p.unit())); people++; }
            for (MovementDtos.Response r : backup.movements()) { if (r.category()==null || r.movementAt()==null || r.person()==null || r.person().name()==null) throw new IllegalArgumentException("Backup contains an invalid movement"); var person = new MovementDtos.PersonRef(null,r.person().armyNo(),r.person().cardNo(),r.person().rank(),r.person().name(),r.person().mobileNo(),r.person().unit()); movements.create(new MovementDtos.Request(r.category(),r.movementAt(),person,r.movementType(),r.remarks(),r.details())); records++; }
            return new BackupDtos.RestoreResult(people,records);
        } catch (IllegalArgumentException e) { throw e; } catch (Exception e) { throw new IllegalArgumentException("Invalid backup JSON"); }
    }
    public void validateDatabaseFile(InputStream input) { Path temporary=copyToTemp(input); try { validateDatabase(temporary); } finally { deleteQuietly(temporary); } }
    @Transactional public BackupDtos.RestoreResult restoreDatabase(InputStream input) { Path temporary=copyToTemp(input); try { validateDatabase(temporary); db.update("ATTACH DATABASE ? AS restore_db",temporary.toString()); try { for(String table:List.of("leave_records","td_posting_records","vehicle_records","outpass_records","night_pass_records","movement_records","personnel")) db.update("DELETE FROM "+table); for(String table:REQUIRED_TABLES) db.update("INSERT INTO "+table+" SELECT * FROM restore_db."+table); } finally { db.execute("DETACH DATABASE restore_db"); } return new BackupDtos.RestoreResult(count("personnel"),count("movement_records")); } finally { deleteQuietly(temporary); } }
    private Path copyToTemp(InputStream input){try{Path temp=Files.createTempFile("rp-gate-restore-", ".db");try(input){Files.copy(input,temp,StandardCopyOption.REPLACE_EXISTING);}return temp;}catch(IOException e){throw new IllegalArgumentException("Unable to read the backup file");}}
    private void validateDatabase(Path path){try(var connection=DriverManager.getConnection("jdbc:sqlite:"+path)){try(var statement=connection.createStatement();var result=statement.executeQuery("PRAGMA integrity_check")){if(!result.next()||!"ok".equalsIgnoreCase(result.getString(1)))throw new IllegalArgumentException("Backup failed SQLite integrity validation");}try(var statement=connection.createStatement();var result=statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table'")){Set<String>tables=new HashSet<>();while(result.next())tables.add(result.getString(1));if(!tables.containsAll(REQUIRED_TABLES))throw new IllegalArgumentException("Backup is missing one or more required tables");}try(var statement=connection.createStatement();var result=statement.executeQuery("PRAGMA foreign_key_check")){if(result.next())throw new IllegalArgumentException("Backup contains invalid foreign-key references");}}catch(IllegalArgumentException e){throw e;}catch(Exception e){throw new IllegalArgumentException("The backup is not a valid SQLite database");}}
    private int count(String table){return db.queryForObject("SELECT count(*) FROM "+table,Integer.class);}
    private String sqlPath(Path path){return path.toAbsolutePath().toString().replace("'","''");}
    private void deleteQuietly(Path path){try{if(path!=null)Files.deleteIfExists(path);}catch(IOException ignored){}}
}
