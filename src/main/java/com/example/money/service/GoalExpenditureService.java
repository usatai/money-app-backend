package com.example.money.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.money.controller.GoalExpenditureForm;
import com.example.money.model.GoalExpenditure;
import com.example.money.repository.GoalExpenditureRepository;
import com.example.money.repository.MoneyRepository;
import com.example.money.repository.UserRepository;

@Service
public class GoalExpenditureService {

    private final GoalExpenditureRepository goalExpenditureRepository;
    private final MoneyRepository moneyRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    public GoalExpenditureService (GoalExpenditureRepository goalExpenditureRepository,
                                   MoneyRepository moneyRepository,
                                   UserRepository userRepository,
                                   MailService mailService) {
        this.goalExpenditureRepository = goalExpenditureRepository;
        this.moneyRepository = moneyRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    // 目標支出額登録（既存の場合は上書き）
    public void save(GoalExpenditureForm goalExpenditureForm,Integer userIdInt) {
        GoalExpenditure existingGoalExpenditure = goalExpenditureRepository.findByUser(userIdInt);
        
        if (existingGoalExpenditure != null) {
            // 既存レコードを更新
            existingGoalExpenditure.setGoal_expenditure(goalExpenditureForm.goal_expenditure());
            existingGoalExpenditure.setUpdated_at(new Date());
            goalExpenditureRepository.save(existingGoalExpenditure);
        } else {
            // 新規作成
            goalExpenditureRepository.save(createGoalExpenditure(goalExpenditureForm,userIdInt));
        }
    }

    private GoalExpenditure createGoalExpenditure(GoalExpenditureForm goalExpenditureForm,Integer userIdInt) {
        GoalExpenditure goalExpenditure = new GoalExpenditure();
        Date now = new Date();

        goalExpenditure.setGoal_expenditure(goalExpenditureForm.goal_expenditure());
        goalExpenditure.setUser_id(userIdInt);
        goalExpenditure.setCreated_at(now);
        goalExpenditure.setUpdated_at(now);

        return goalExpenditure;
    }

    // 今月初ログインかそうでないか
    public boolean firstLoginCheck(Integer userIdInt) {
        Long count = goalExpenditureRepository.findLoginCheck(userIdInt);
        return count != null && count > 0;
    }

    @Scheduled(cron = "0 0 9,21 * * ?", zone = "Asia/Tokyo")
    // @Scheduled(cron = "0 * * * * *",zone = "Asia/Tokyo")
    @Transactional
    public void notificationExpenditure() {
        List<Object[]> resultList = moneyRepository.sumExpenditureByUserWithGoal();
        Map<Integer,Long> userExpenditureMap = new HashMap<>();
        for (Object[] row : resultList) {
            Integer userIdInt = ((Number) row[0]).intValue();
            Long total = ((Number) row[1]).longValue(); 
            userExpenditureMap.put(userIdInt,total);
        }

        List<GoalExpenditure> goalExpenditureList = goalExpenditureRepository.findAll();
        for (GoalExpenditure goalExpenditure : goalExpenditureList) {
            if (userExpenditureMap.containsKey(goalExpenditure.getUser_id())) {
                LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
                LocalDate today = LocalDate.now();

                long elapsedDays = ChronoUnit.DAYS.between(firstDay, today) + 1;
                double idealDailySpend =  goalExpenditure.getGoal_expenditure() / firstDay.lengthOfMonth(); // 月の日数で割る
                double idealTotalSpend = idealDailySpend * elapsedDays;

                double actualTotalSpend = userExpenditureMap.get(goalExpenditure.getUser_id());
                double overspendRate = (actualTotalSpend - idealTotalSpend) / idealTotalSpend;
                double projectedSpend = (actualTotalSpend / elapsedDays) * firstDay.lengthOfMonth();
                double remainingBudget = goalExpenditure.getGoal_expenditure() - actualTotalSpend;
                long remainingDays = firstDay.lengthOfMonth() - elapsedDays;

                if (overspendRate > 0.2) {
                    userRepository.findById(goalExpenditure.getUser_id()).ifPresent(user -> {
                        String email = user.getUser_email();
                        String subject = "家計簿サービス「マネカン」支出目標警告";
                        String body = String.format(
                            "今月の支出ペースが目標より %.0f%% 上回っています。\n" + 
                            "このペースだと今月の支出は約 %.0f 円に着地する見込みです（目標は %.0f 円）。\n" +
                            "残り%d日で使える金額はあと %.0f 円です。\n" +
                            "計画的に使って目標達成を目指しましょう！応援しています😊", 
                            overspendRate * 100,
                            projectedSpend,
                            (double)goalExpenditure.getGoal_expenditure(),
                            remainingDays,
                            remainingBudget
                        );
                        mailService.sendNotification(email, subject, body);
                    });
                }
            }
        }
    }
}
