package com.monisuea.numbergame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NumberGameService {
    private static final String NUMBERS_KEY = "numbers";
    private static final String NUM_STUDENTS_KEY = "numStudents";
    private static final String MAX_PARTICIPANTS_KEY = "maxParticipants";
    private static final String PRIZE_KEY = "prize";
    private static final String GAME_ENDED_KEY = "gameEnded";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public NumberResponse submitNumber(int number) {
        List<Integer> numbers = getNumbers();
        numbers.add(number);
        redisTemplate.opsForValue().set(NUMBERS_KEY, numbers);

        int numStudents = getNumStudents() + 1;
        redisTemplate.opsForValue().set(NUM_STUDENTS_KEY, numStudents);

        return new NumberResponse("success", "提交成功，等待其他学生提交。", null, 0.0, numStudents, getMaxParticipants(), 0);
    }

    public NumberResponse calculateResult() {
        List<Integer> numbers = getNumbers();
        if (!numbers.isEmpty()) {
            double average = calculateAverage(numbers);
            double targetNumber = average * (2.0 / 3);
            int closestIndex = findClosestToAverageThird(numbers, targetNumber);
            double prize = calculatePrize(numbers);
            int numWinners = calculateWinners(numbers, targetNumber);
            return new NumberResponse("success", "计算已完成，以下是结果。", numbers.get(closestIndex), prize, getNumStudents(), getMaxParticipants(), numWinners);
        } else {
            return new NumberResponse("error", "所有学生还未提交完毕。", null, 0.0, getNumStudents(), getMaxParticipants(), 0);
        }
    }

    private List<Integer> getNumbers() {
        List<Integer> numbers = (List<Integer>) redisTemplate.opsForValue().get(NUMBERS_KEY);
        return numbers != null ? numbers : new ArrayList<>();
    }

    private int getNumStudents() {
        Integer numStudents = (Integer) redisTemplate.opsForValue().get(NUM_STUDENTS_KEY);
        return numStudents != null ? numStudents : 0;
    }

    private int getMaxParticipants() {
        Integer maxParticipants = (Integer) redisTemplate.opsForValue().get(MAX_PARTICIPANTS_KEY);
        return maxParticipants != null ? maxParticipants : 10;
    }

    private double calculateAverage(List<Integer> numbers) {
        return numbers.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    private int findClosestToAverageThird(List<Integer> numbers, double targetNumber) {
        int closestIndex = 0;
        double minDifference = Double.MAX_VALUE;
        for (int i = 0; i < numbers.size(); i++) {
            double difference = Math.abs(numbers.get(i) - targetNumber);
            if (difference < minDifference) {
                minDifference = difference;
                closestIndex = i;
            }
        }
        return closestIndex;
    }

    private double calculatePrize(List<Integer> numbers) {
        return numbers.stream().mapToInt(Integer::intValue).sum() / (double) getMaxParticipants();
    }

    private int calculateWinners(List<Integer> numbers, double targetNumber) {
        int numWinners = 0;
        double minDifference = Double.MAX_VALUE;
        for (int number : numbers) {
            double difference = Math.abs(number - targetNumber);
            if (difference < minDifference) {
                minDifference = difference;
                numWinners = 1; // 重置赢家人数为1
            } else if (difference == minDifference) {
                numWinners++; // 如果有多个数字与目标数字的差值相同，则增加赢家人数
            }
        }
        return numWinners;
    }
}