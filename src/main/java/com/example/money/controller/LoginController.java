package com.example.money.controller;

import com.example.money.enums.IncomeExpenditureType;
import com.example.money.service.LabelService;
import com.example.money.service.MoneyService;
import com.example.money.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    LabelService labelService;

    @Autowired
    MoneyService moneyService;

    @GetMapping("/")
    public String animationPage(){
        return "animation";
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        return "home";
    }

    @GetMapping("money")
    public ResponseEntity<?> money(@RequestParam(name="type",required = false)IncomeExpenditureType type,Model model, HttpSession session) {
        Integer userIdInt = (Integer) session.getAttribute("userIdInt");

        //URLからの直接ログインを防ぐ
        if(userIdInt == null){
            return ResponseEntity.badRequest().body(Map.of("errors","URLからの直接ログインはできません。"));
        }

        //バリデーションメッセージ
        if(!model.containsAttribute("moneyForm")){
            model.addAttribute("moneyForm",new MoneyForm(null,null,null,userIdInt,null));
        }

        String now = (String) session.getAttribute("now");
        YearMonth yearMonth = (YearMonth) session.getAttribute("currentDate");
        String formatYearMonth = yearMonth.toString();
        LocalDate currentDate = LocalDate.now();
        int monthDate = currentDate.getMonthValue();
        Integer currentMonth = (Integer) session.getAttribute("currentMonth");

        //ユーザーIDが一致するlabel_nameを抽出し、新たなリストに入れる
        List<String> userLabel = labelService.getUserOfLabel(userIdInt,monthDate,currentMonth,type);

        return ResponseEntity.ok(Map.of(
                "now",now,
                "formatYearMonth",formatYearMonth,
                "userIdInt",userIdInt,
                "userLabel",userLabel
        ));
    }

    @GetMapping("main")
    public ResponseEntity<?> mainBack(@RequestParam(name="selectMonth",required = false) String monthList,
                                      @RequestParam(name="type",required = false) IncomeExpenditureType type,
                                      Model model,
                                      HttpSession session) {

        Integer userIdInt = (Integer) session.getAttribute("userIdInt");

        //URLからの直接ログインを防ぐ
        if(userIdInt == null){
            return ResponseEntity.badRequest().body(Map.of("errors","URLからの直接ログインはできません。"));
        }

        final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-M");
        final YearMonth currentDate = (monthList != null) ? YearMonth.parse(monthList,format) : YearMonth.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();

        LocalDate localDate = LocalDate.now();
        String now = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //ユーザーIDが一致するlabel_nameを抽出し、新たなリストに入れる
        List<String> labelList = labelService.getLabelNamesAndMonth(userIdInt,currentYear,currentMonth,type);

        //各ユーザーの月毎のmoney_priceを取得する
        Map<String,Integer> moneyMap = moneyService.getMoneyListAndMonth(userIdInt,currentYear,currentMonth,type);
        //money_priceに入っていないデータはデフォルトで0円としてリストに追加する
        List<Integer> moneyList = labelList.stream()
                .map(label -> moneyMap.getOrDefault(label,0))
                .toList();

        //現在の月と同じ月のデータを抽出
        //moneyテーブルを全件抽出
        List<String> moneyDate = moneyService.getMoneyDate(userIdInt,currentYear,currentMonth,type);

        //各ユーザーのmoney_priceがある日時を年月ごとに取得し、その日時ごとのmoney_priceの合計を取得
        Map<Integer,Integer> moneyNowMap = moneyService.getMoneyMonthOfDaySumming(userIdInt,currentYear,currentMonth,type);

        //そのMapのvalue(日時ごとの合計数値)を新たなリストに追加
        List<Integer> moneyNowList = new ArrayList<>(moneyNowMap.values());

        //各ユーザーのデータのある年月を取得
        List<String> userMonthList = moneyService.getUserOfDateList(userIdInt);

        //main.htmlから月選択データが飛んできたらその年月をmain.htmlに返す
        if(monthList != null){
            String selectMonth = moneyService.getYearMonthData(userIdInt,currentYear,currentMonth);
            model.addAttribute("selectMonth",selectMonth);
        }

        //月間の合計値を取得
        int moneySum = moneyList.stream()
                .mapToInt(money -> money)
                .sum();

        session.setAttribute("now",now);
        session.setAttribute("currentDate",currentDate);
        session.setAttribute("currentMonth",currentMonth);
        session.setAttribute("userIdInt", userIdInt);

        return ResponseEntity.ok(Map.of(
            "moneyNowList", moneyNowList,
            "moneyDate", moneyDate,
            "moneyList", moneyList,
            "labelList", labelList,
            "userMonthList", userMonthList,
            "monthDate", currentDate,
            "moneySum", moneySum
        ));
    }
}

