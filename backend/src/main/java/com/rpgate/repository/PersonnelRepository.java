package com.rpgate.repository;

import com.rpgate.dto.PersonnelDtos;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PersonnelRepository {
    private final JdbcTemplate db;
    public PersonnelRepository(JdbcTemplate db) { this.db = db; }
    private PersonnelDtos.Response map(java.sql.ResultSet r, int n) throws java.sql.SQLException { return new PersonnelDtos.Response(r.getLong("personnel_id"),r.getString("army_no"),r.getString("card_no"),r.getString("rank"),r.getString("name"),r.getString("mobile_no"),r.getString("unit"),r.getString("status"),r.getString("created_at"),r.getString("updated_at")); }
    public List<PersonnelDtos.Response> list(String search) { String q = "%" + (search == null ? "" : search.trim().toLowerCase()) + "%"; return db.query("SELECT * FROM personnel WHERE lower(coalesce(army_no,'')) LIKE ? OR lower(coalesce(card_no,'')) LIKE ? OR lower(name) LIKE ? OR lower(coalesce(rank,'')) LIKE ? OR lower(coalesce(unit,'')) LIKE ? ORDER BY name", this::map, q, q, q, q, q); }
    public PersonnelDtos.Response get(long id) { try { return db.queryForObject("SELECT * FROM personnel WHERE personnel_id=?", this::map, id); } catch (EmptyResultDataAccessException e) { return null; } }
    public PersonnelDtos.Response findByArmyOrCard(String armyNo, String cardNo) { try { return db.queryForObject("SELECT * FROM personnel WHERE (army_no IS NOT NULL AND lower(trim(army_no))=lower(trim(?))) OR (card_no IS NOT NULL AND lower(trim(card_no))=lower(trim(?))) ORDER BY personnel_id LIMIT 1", this::map, armyNo, cardNo); } catch (EmptyResultDataAccessException e) { return null; } }
    public long insert(PersonnelDtos.Request x) { return db.queryForObject("INSERT INTO personnel(army_no,card_no,rank,name,mobile_no,unit) VALUES(?,?,?,?,?,?) RETURNING personnel_id", Long.class, clean(x.armyNo()),clean(x.cardNo()),clean(x.rank()),x.name().trim(),clean(x.mobileNo()),clean(x.unit())); }
    public int update(long id, PersonnelDtos.Request x) { return db.update("UPDATE personnel SET army_no=?,card_no=?,rank=?,name=?,mobile_no=?,unit=? WHERE personnel_id=?",clean(x.armyNo()),clean(x.cardNo()),clean(x.rank()),x.name().trim(),clean(x.mobileNo()),clean(x.unit()),id); }
    public int delete(long id) { return db.update("UPDATE personnel SET status='INACTIVE' WHERE personnel_id=?", id); }
    public void clear() { db.update("UPDATE personnel SET status='INACTIVE'"); }
    private String clean(String s) { return s == null || s.trim().isEmpty() ? null : s.trim(); }
}
