package com.forcetower.uefs.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
@ForeignKey(entity = Discipline.class, parentColumns = "uid", childColumns = "discipline", onDelete = CASCADE, onUpdate = CASCADE)
public class DisciplineGroup {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int discipline;
    private String teacher;
    private String group;
    private int credits;
    private int missLimit;
    private String classPeriod = "";
    private String department = "";
    private boolean draft = true;
    private int ignored = 0;

    @Ignore
    private String semester;
    @Ignore
    private String code;

    public DisciplineGroup(int discipline, String teacher, String group, int credits, int missLimit, String classPeriod, String department) {
        this.discipline = discipline;
        this.teacher = teacher;
        this.group = group;
        this.credits = credits;
        this.missLimit = missLimit;
        this.classPeriod = classPeriod;
        this.department = department;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getDiscipline() {
        return discipline;
    }

    public void setDiscipline(int discipline) {
        this.discipline = discipline;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getMissLimit() {
        return missLimit;
    }

    public void setMissLimit(int missLimit) {
        this.missLimit = missLimit;
    }

    public String getClassPeriod() {
        return classPeriod;
    }

    public void setClassPeriod(String classPeriod) {
        this.classPeriod = classPeriod;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public void setDisciplineCodeAndSemester(String code, String semester) {
        this.code = code;
        this.semester = semester;
    }

    public String getSemester() {
        return semester;
    }

    public String getCode() {
        return code;
    }

    public void selectiveCopy(DisciplineGroup other) {
        if (other == null) return;

        if (validString(other.teacher)      || teacher == null)     teacher     = other.teacher;
        if (validString(other.group)        || group == null)       group       = other.group;
        if (validString(other.classPeriod)  || classPeriod == null) classPeriod = other.classPeriod;
        if (validString(other.department)   || department == null)  department  = other.department;
        if (other.credits > 0   || credits == 0)    credits     = other.credits;
        if (other.missLimit > 0 || missLimit == 0)  missLimit   = other.missLimit;
    }

    @Override
    public String toString() {
        return group + " - d_id: " + discipline;
    }

    public int getIgnored() {
        return ignored;
    }

    public void setIgnored(int ignored) {
        this.ignored = ignored;
    }
}
