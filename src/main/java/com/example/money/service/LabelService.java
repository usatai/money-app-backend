package com.example.money.service;

import com.example.money.controller.DeleteForm;
import com.example.money.controller.LabelForm;
import com.example.money.enums.IncomeExpenditureType;
import com.example.money.model.Label;
import com.example.money.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class LabelService {

    @Autowired
    LabelRepository labelrepository;

    //各ユーザーのラベルを月毎に取得
    public List<String> getLabelNamesAndMonth(int userIdInt,int currentYear,int currentMonth,IncomeExpenditureType type){
       List<Label> labelTable = labelrepository.findAll();
       return labelTable.stream()
               .filter(label -> label.getUser_id() == userIdInt)
               .filter(label -> label.getIncomeExpenditureType() == type)
               .filter(date ->{
                    LocalDate createDate = date.getCreate_date().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    int createYear = createDate.getYear();
                    int createMonth = createDate.getMonthValue();

                    return createYear == currentYear && createMonth == currentMonth;
                })
               .map(Label::getLabel_name)
               .toList();
    }

    //ユーザーIDが一致するlabel_nameを抽出し、新たなリストに入れる
    public List<String> getUserOfLabel(int userIdInt,int monthDate,Integer currentMonth,IncomeExpenditureType type){
        List<Label> labelTable = labelrepository.findAll();
        return labelTable.stream()
                .filter(label -> label.getUser_id() == userIdInt)
                .filter(label -> label.getIncomeExpenditureType() == type)
                .filter(date ->{
                    LocalDate createDate = date.getCreate_date().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    int targetMonth = (currentMonth == null) ? monthDate : currentMonth;
                    return createDate.getMonthValue() == targetMonth;
                })
                .map(Label::getLabel_name)
                .toList();
    }


    public List<Label> search(){
        return labelrepository.findAll();
    }

    public void input(LabelForm labelForm, Integer userIdInt, YearMonth yearMonth) {
        labelrepository.save(inputLabel(labelForm,userIdInt,yearMonth));
    }

    private Label inputLabel(LabelForm labelForm,Integer userIdInt,YearMonth yearMonth){
        Label label = new Label();
        Date now = new Date();

        label.setLabel_name(labelForm.label_name());
        label.setUser_id(userIdInt);
        label.setIncomeExpenditureType(labelForm.type());

        LocalDate localDate = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        YearMonth dateYearMonth = YearMonth.from(localDate);

        if(dateYearMonth.equals(yearMonth)){
            label.setCreate_date(now);
        }else{
            LocalDate selectMonth = yearMonth.atDay(1);
            Date selectDate = Date.from(selectMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
            label.setCreate_date(selectDate);
        }

        return label;
    }

    public void deleteLabel(Integer userIdInt,DeleteForm deleteForm,String yearMonthSt) {
        labelrepository.deleteLabel(userIdInt,label_idGet(userIdInt,deleteForm.label_name(),yearMonthSt),yearMonthSt);
    }

    private Integer label_idGet(Integer userIdInt,String label_name,String yearMonthSt){
        return labelrepository.searchLabel_id(userIdInt,label_name,yearMonthSt);
    }
}
