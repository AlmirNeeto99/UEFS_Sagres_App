package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.ADisciplineDao;
import com.forcetower.uefs.database.entities.ADiscipline;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class DisciplineRepository {
    private final ADisciplineDao dao;

    public DisciplineRepository(ADisciplineDao dao) {
        this.dao = dao;
    }

    public List<ADiscipline> getAllDisciplines() {
        return dao.getAllDisciplines();
    }

    public List<ADiscipline> getDisciplinesFromSemester(String semester) {
        return dao.getDisciplinesFromSemester(semester);
    }

    public List<ADiscipline> getDisciplinesByCode(String code) {
        return dao.getDisciplinesByCode(code);
    }

    public ADiscipline getDisciplinesBySemesterAndCode(String semester, String code) {
        return dao.getDisciplinesBySemesterAndCode(semester, code);
    }

    public void insertDiscipline(ADiscipline... disciplines) {
        dao.insertDiscipline(disciplines);
    }

    public void deleteDiscipline(ADiscipline discipline) {
        dao.deleteDiscipline(discipline);
    }

    public void deleteAllDisciplines() {
        dao.deleteAllDisciplines();
    }
}
