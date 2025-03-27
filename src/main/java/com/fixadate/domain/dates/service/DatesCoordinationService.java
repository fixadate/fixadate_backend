package com.fixadate.domain.dates.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatesCoordinationService {

    // push 알림을 통해서
    public void getDatesCollectionList(){
        if(checkValidations()){

        }
        if(checkHasAdate()){

        }
    }

    public void setDatesCollection(){
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


        boolean isCreated = true;

        if(isCreated){
            // 미팅 참석 예정자들에게 노티 알림
            sendDatesConfirmation();

            // 해당 워크스페이스에 스케쥴로 등록
        }

    }

    public void getDatesCollectionsResult(){
        if(isTeamsOwner()){

        }

        if(isAlreadyConfirmed()){

        }

    }


    private void sendDatesConfirmation(){


    }



    private boolean checkValidations(){
        return isTeamMember() && isAlreadyConfirmed();
    }

    private boolean isTeamMember(){
        return true;
    }

    private boolean isAlreadyConfirmed(){
        return true;
    }

    private boolean checkHasAdate(){
        return true;
    }

    private boolean isTeamsOwner(){
        return true;
    }

}
