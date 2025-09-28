package com.example.money.service;

import com.example.money.controller.MoneyForm;
import com.example.money.enums.IncomeExpenditureType;
import com.example.money.model.Label;
import com.example.money.model.Money;
import com.example.money.repository.LabelRepository;
import com.example.money.repository.MoneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MoneyService {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");

    @Autowired
    MoneyRepository moneyrepository;

    @Autowired
    LabelRepository labelRepository;

    LocalDate nowDate = LocalDate.now();
    String currentMonth = nowDate.getYear() + "-" + nowDate.getMonthValue();

    //各ユーザーの月ごとのmoney_priceをlabelTableのlabel_idと紐付けて取得
    public Map<String,Integer> getMoneyListAndMonth(int userIdInt,int currentYear,int currentMonth,IncomeExpenditureType type){
        List<Money> moneyTable = moneyrepository.findAll();
        List<Label> labelTable = labelRepository.findAll();
        
        // TOTALの場合は収入、支出、収支合計をそれぞれ返す
        if (type == IncomeExpenditureType.TOTAL) {
            // ユーザーIDと年月でフィルターしたデータを取得
            List<Money> filteredMoney = moneyTable.stream()
                    .filter(money -> {
                        // ユーザーIDのフィルター
                        boolean userMatch = money.getUser_id() == userIdInt;
                        return userMatch;
                    })
                    .filter(money -> {
                        // 年月のフィルター
                        LocalDate createDate = money.getCreated_date().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        int createYear = createDate.getYear();
                        int createMonth = createDate.getMonthValue();

                        boolean dateMatch = createYear == currentYear && createMonth == currentMonth;
                        return dateMatch;
                    })
                    .toList();
            
            // 収入の合計を計算
            int incomeAmount = filteredMoney.stream()
                    .filter(money -> money.getIncomeExpenditureType() == IncomeExpenditureType.INCOME)
                    .mapToInt(Money::getMoney_price)
                    .sum();
            
            // 支出の合計を計算
            int expenditureAmount = filteredMoney.stream()
                    .filter(money -> money.getIncomeExpenditureType() == IncomeExpenditureType.EXPENDITURE)
                    .mapToInt(Money::getMoney_price)
                    .sum();
            
            // 収支合計を計算（収入 - 支出）
            int totalAmount = incomeAmount - expenditureAmount;
            
            Map<String, Integer> result = new HashMap<>();
            result.put("収入", incomeAmount);
            result.put("支出", expenditureAmount);
            result.put("収支合計", totalAmount);
            return result;
        } else {
            // INCOME または EXPENDITURE の場合は従来通りの処理
            return moneyTable.stream()
                    .filter(money -> {
                        // ユーザーIDのフィルター
                        boolean userMatch = money.getUser_id() == userIdInt;
                        return userMatch;
                    })
                    .filter(money -> {
                        IncomeExpenditureType moneyType = money.getIncomeExpenditureType();
                        boolean typeMatch = (moneyType != null) && (moneyType == type); // nullチェックを追加
                        return typeMatch;
                    })
                    .filter(money -> {
                        // 年月のフィルター
                        LocalDate createDate = money.getCreated_date().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        int createYear = createDate.getYear();
                        int createMonth = createDate.getMonthValue();

                        boolean dateMatch = createYear == currentYear && createMonth == currentMonth;
                        return dateMatch;
                    })
                    .flatMap(money -> labelTable.stream()
                            .filter(label -> label.getLabel_id() == money.getLabel_id())
                            .map(label -> new AbstractMap.SimpleEntry<>(label.getLabel_name(), money.getMoney_price()))
                    )
                    .collect(Collectors.groupingBy(
                            AbstractMap.SimpleEntry::getKey,
                            Collectors.summingInt(AbstractMap.SimpleEntry::getValue)
                    ));
        }
    }

    //money_priceが存在している各月毎の日時を取得
    public List<String> getMoneyDate(int userIdInt,int currentYear,int currentMonth,IncomeExpenditureType type){
        if (type == IncomeExpenditureType.TOTAL) {
            return Collections.emptyList();
        }

        List<Money> moneyTable = moneyrepository.findAll();
        return moneyTable.stream()
                .filter(money -> money.getUser_id() == userIdInt)
                .filter(money -> money.getIncomeExpenditureType() == type)
                //その抽出したデータのcreate_date(日付)を取得
                .map(Money::getCreated_date)
                .map(money -> money.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .filter(money -> money.getYear() == currentYear && money.getMonthValue() == currentMonth)
                .distinct()
                .sorted()
                .map(formatter::format)
                .toList();
    }

    //各ユーザーのmoney_priceが入っている年月データを取得し、その年月の日時ごとにmoney_priceを合計した数値を取得
    public Map<Integer,Integer> getMoneyMonthOfDaySumming(int userIdInt,int currentYear,int currentMonth,IncomeExpenditureType type){
        List<Money> moneyTable = moneyrepository.findAll();
        return moneyTable.stream()
                .filter(money -> money.getUser_id() == userIdInt)
                .filter(money -> money.getIncomeExpenditureType() == type)
                .filter(money ->{
                    LocalDate createDate = money.getCreated_date().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    int createYear = createDate.getYear();
                    int createMonth = createDate.getMonthValue();

                    return createYear == currentYear && createMonth == currentMonth;
                })
                .collect(Collectors.groupingBy(
                        money -> {
                            LocalDate createDate = money.getCreated_date().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            return createDate.getDayOfMonth();
                        },
                        TreeMap::new,
                        Collectors.summingInt(Money::getMoney_price)
                ));
    }

    //ユーザーごとのデータのある年月日時を取得
    public List<String> getUserOfDateList(int userIdInt){
        List<Money> moneyTable = moneyrepository.findAll();
        ArrayList<String> moneyDateList = moneyTable.stream()
            .filter(date -> date.getUser_id() == userIdInt)
            .map(Money::getCreated_date)
            .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
            .map(date -> date.getYear() + "-"  + date.getMonthValue())
            .distinct()
            .collect(Collectors.toCollection(ArrayList::new));

        if(!moneyDateList.contains(currentMonth)){
            moneyDateList.add(currentMonth);
        }

        return moneyDateList;
    }

    public String getYearMonthData(int userIdInt,int currentYear,int currentMonth){
        List<Money> moneyTable = moneyrepository.findAll();
        return moneyTable.stream()
                .filter(date -> date.getUser_id() == userIdInt)
                .map(Money::getCreated_date)
                .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .filter(date -> date.getYear() == currentYear && date.getMonthValue() == currentMonth)
                .map(date -> date.getYear() + "-" + date.getMonthValue())
                .findFirst()
                .orElse(null);
    }


    public List<Money> searchAll(){
        return moneyrepository.findAll();
    }

    public void moneyInput(MoneyForm moneyForm,Integer userIdInt){
        moneyrepository.save(moneyCreate(moneyForm,userIdInt));
    }

    private Money moneyCreate(MoneyForm moneyForm,Integer userIdInt) {
        Money money = new Money();

        LocalDate date = LocalDate.parse(moneyForm.date(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Date sqlDate = Date.valueOf(date);

        money.setMoney_price(moneyForm.money_price());
        System.out.println(moneyForm.incomeExpenditureType());
        money.setIncomeExpenditureType(moneyForm.incomeExpenditureType());
        money.setUser_id(userIdInt);
        money.setLabel_id(userGetLabelId(userIdInt,moneyForm.label_name(),sqlDate));
        money.setCreated_date(sqlDate);

        return money;
    }

    private Integer userGetLabelId(Integer userIdInt, String label_name, Date sqlDate) {
        return moneyrepository.userGetLabelId(userIdInt,label_name,sqlDate);
    }
}
