package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.rep.ScheduleRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class ScheduleViewModel extends ViewModel {
    private LiveData<List<DisciplineClassLocation>> disciplineLocations;
    private final ScheduleRepository repository;

    @Inject
    ScheduleViewModel(ScheduleRepository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("SameParameterValue")
    public LiveData<List<DisciplineClassLocation>> getSchedule(String semester) {
        if (disciplineLocations == null) {
            disciplineLocations = repository.getSchedule(semester);
        }

        return disciplineLocations;
    }
}
