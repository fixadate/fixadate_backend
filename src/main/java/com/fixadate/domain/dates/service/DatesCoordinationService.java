package com.fixadate.domain.dates.service;

import com.fixadate.domain.dates.repository.DatesCollectionsRepository;
import com.fixadate.domain.dates.repository.DatesCoordinationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatesCoordinationService {
    private final DatesCollectionsRepository datesCollectionsRepository;
    private final DatesCoordinationsRepository datesCoordinationsRepository;

    // push 알림을 통해서
    public void getDatesCoordination(){
        if(checkValidations()){

        }
        if(checkHasAdate()){

        }
    }

    public void setDatesCoordination(){
        if(checkValidations()){

        }
        if(checkHasAdate()){

        }
    }

    public void confirmDatesCoordination(){
        if(isTeamsOwner()){

        }

        if(isAlreadyConfirmed()){

        }

        if(isTeamsOwner()){

        }
    }

    public boolean checkValidations(){
        return isTeamMember() && isAlreadyConfirmed();
    }

    public boolean isTeamMember(){
        return true;
    }

    public boolean isAlreadyConfirmed(){
        return true;
    }

    public boolean checkHasAdate(){
        return true;
    }

    public boolean isTeamsOwner(){
        return true;
    }

}
