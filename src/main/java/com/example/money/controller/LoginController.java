package com.example.money.controller;

import com.example.money.enums.IncomeExpenditureType;
import com.example.money.service.LabelService;
import com.example.money.service.MoneyService;
import com.example.money.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class LoginController {

    private final UserService userService;
    private final LabelService labelService;
    private final MoneyService moneyService;

    public LoginController (UserService userService,LabelService labelService,MoneyService moneyService) {
        this.userService = userService;
        this.labelService = labelService;
        this.moneyService = moneyService;
    }

    @GetMapping("/")
    public String animationPage(){
        return "animation";
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        return "home";
    }

    @GetMapping("/money")
    public ResponseEntity<?> money(@RequestParam(name="type",required = false)IncomeExpenditureType type,
                                   @RequestParam (name = "nowDate")String nowDate, @RequestParam (name = "currentMonth")Integer currentMonth, Model model,Principal principal) {

        
        String username = principal.getName(); // トークン内のユーザー名
    
        Integer userIdInt = userService.getUserIdByUsername(username)
            .orElseThrow(() -> new RuntimeException("ユーザーIDが見つかりません: " + username));

        //バリデーションメッセージ
        if(!model.containsAttribute("moneyForm")){
            model.addAttribute("moneyForm",new MoneyForm(null,null,null,userIdInt,null,null,null));
        }

        System.out.println("日付" + currentMonth);
        String now = nowDate;
        LocalDate currentDate = LocalDate.now();
        int monthDate = currentDate.getMonthValue();

        //ユーザーIDが一致するlabel_nameを抽出し、新たなリストに入れる
        List<String> userLabel = labelService.getUserOfLabel(userIdInt,monthDate,currentMonth,type);

        return ResponseEntity.ok(Map.of(
                "now",now,
                "userIdInt",userIdInt,
                "userLabel",userLabel
        ));
    }

    @GetMapping("main")
    public ResponseEntity<?> mainBack(@RequestParam(name="selectMonth",required = false) String monthList,
                                      @RequestParam(name="type",required = false) IncomeExpenditureType type,
                                      Model model,
                                      HttpSession session) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    
        String username = authentication.getName(); // トークン内のユーザー名
        Integer userIdInt = userService.getUserIdByUsername(username)
            .orElseThrow(() -> new RuntimeException("ユーザーIDが見つかりません: " + username));

        final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-M");
        final YearMonth currentDate = (monthList != null) ? YearMonth.parse(monthList,format) : YearMonth.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();

        //ユーザーIDが一致するlabel_nameを抽出し、新たなリストに入れる
        List<String> labelList = labelService.getLabelNamesAndMonth(userIdInt,currentYear,currentMonth,type);

        //各ユーザーの月毎のmoney_priceを取得する
        Map<String,Integer> moneyMap = moneyService.getMoneyListAndMonth(userIdInt,currentYear,currentMonth,type);
        System.out.println(moneyMap);
        
        List<Integer> moneyList;
        List<String> finalLabelList;
        
        // TOTALの場合は収支合計のみを扱う
        if (type == IncomeExpenditureType.TOTAL) {
            // 収支合計の値のみを取得
            Integer totalAmount = moneyMap.get("収支合計");
            System.out.println(totalAmount);
            moneyList = List.of(totalAmount != null ? totalAmount : 0);
            finalLabelList = List.of("収支合計");
        } else {
            //money_priceに入っていないデータはデフォルトで0円としてリストに追加する
            moneyList = labelList.stream()
                    .map(label -> moneyMap.getOrDefault(label,0))
                    .toList();
            finalLabelList = labelList;
        }

        //現在の月と同じ月のデータを抽出
        //moneyテーブルを全件抽出
        List<String> moneyDate = moneyService.getMoneyDate(userIdInt,currentYear,currentMonth,type);

        //各ユーザーのmoney_priceがある日時を年月ごとに取得し、その日時ごとのmoney_priceの合計を取得
        Map<Integer,Integer> moneyNowMap = moneyService.getMoneyMonthOfDaySumming(userIdInt,currentYear,currentMonth,type);

        //そのMapのvalue(日時ごとの合計数値)を新たなリストに追加
        List<Integer> moneyNowList;
        if (type == IncomeExpenditureType.TOTAL) {
            // TOTALの場合は空配列を返す
            moneyNowList = new ArrayList<>();
        } else {
            moneyNowList = new ArrayList<>(moneyNowMap.values());
        }

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

        return ResponseEntity.ok(Map.of(
            "moneyNowList", moneyNowList,
            "moneyDate", moneyDate,
            "moneyList", moneyList,
            "labelList", finalLabelList,
            "userMonthList", userMonthList,
            "monthDate", currentDate,
            "moneySum", moneySum
        ));
    }
}

