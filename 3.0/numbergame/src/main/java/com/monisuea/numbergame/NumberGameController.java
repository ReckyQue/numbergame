package com.monisuea.numbergame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
public class NumberGameController {
    private static final String NUMBERS_KEY = "numbers";
    private static final String NUM_STUDENTS_KEY = "numStudents";
    private static final String MAX_PARTICIPANTS_KEY = "maxParticipants";
    private static final String PRIZE_KEY = "prize";
    private static final String GAME_ENDED_KEY = "gameEnded";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private NumberGameService numberGameService;

    @PostConstruct
    public void init() {
        // 初始化 Redis 中的默认值
        if (redisTemplate.opsForValue().get(NUM_STUDENTS_KEY) == null) {
            redisTemplate.opsForValue().set(NUM_STUDENTS_KEY, 0);
        }
        if (redisTemplate.opsForValue().get(MAX_PARTICIPANTS_KEY) == null) {
            redisTemplate.opsForValue().set(MAX_PARTICIPANTS_KEY, 10); // 默认参与人数为 10
        }
        if (redisTemplate.opsForValue().get(GAME_ENDED_KEY) == null) {
            redisTemplate.opsForValue().set(GAME_ENDED_KEY, false); // 初始化游戏结束标志
        }
    }

    @PostMapping("/setParticipants")
    public @ResponseBody NumberResponse setParticipants(@RequestBody NumberRequest request) {
        try {
            int participants = request.getNumber();
            if (participants < 1) {
                return new NumberResponse("error", "参与人数必须大于0。", null, 0.0, getNumStudents(), getMaxParticipants(), 0);
            }
            redisTemplate.opsForValue().set(MAX_PARTICIPANTS_KEY, participants);
            return new NumberResponse("success", "参与人数已设置为 " + participants + "。", null, 0.0, getNumStudents(), getMaxParticipants(), 0);
        } catch (Exception e) {
            return new NumberResponse("error", "服务器错误", null, 0.0, getNumStudents(), getMaxParticipants(), 0);
        }
    }

    @GetMapping("/getParticipants")
    public @ResponseBody NumberResponse getParticipants() {
        try {
            int participants = getMaxParticipants();
            return new NumberResponse("success", "当前参与人数设置为 " + participants + "。", null, 0.0, getNumStudents(), getMaxParticipants(), 0);
        } catch (Exception e) {
            return new NumberResponse("error", "服务器错误", null, 0.0, getNumStudents(), getMaxParticipants(), 0);
        }
    }

    @PostMapping("/submit")
    public @ResponseBody NumberResponse submitNumber(@RequestBody NumberRequest request) {
        try {
            int number = request.getNumber();
            if (number >= 1 && number <= 100) {
                NumberResponse response = numberGameService.submitNumber(number);
                if (response.getStatus().equals("success") && getNumStudents() == getMaxParticipants()) {
                    NumberResponse result = numberGameService.calculateResult();
                    resetGame();
                    return result;
                }
                return response;
            } else {
                return new NumberResponse("error", "数字必须在1到100之间。", null, 0.0, getNumStudents(), getMaxParticipants(), 0);
            }
        } catch (Exception e) {
            return new NumberResponse("error", "服务器错误", null, 0.0, getNumStudents(), getMaxParticipants(), 0);
        }
    }

    @GetMapping("/result")
    public @ResponseBody NumberResponse getResult() {
        try {
            return numberGameService.calculateResult();
        } catch (Exception e) {
            return new NumberResponse("error", "服务器错误", null, 0.0, getNumStudents(), getMaxParticipants(), 0);
        }
    }

    @GetMapping("/status")
    public @ResponseBody NumberResponse getStatus() {
        boolean gameEnded = (boolean) redisTemplate.opsForValue().get(GAME_ENDED_KEY);
        if (gameEnded) {
            NumberResponse result = getResult();
            result.setMessage("游戏已结束，以下是最终结果：");
            return result;
        } else {
            return new NumberResponse("success", "游戏进行中", null, 0.0, getNumStudents(), getMaxParticipants(), 0);
        }
    }

    @PostMapping("/reset")
    public @ResponseBody NumberResponse resetGame() {
        try {
            // 清除 Redis 中的所有相关数据
            redisTemplate.delete(NUMBERS_KEY);
            redisTemplate.delete(NUM_STUDENTS_KEY);
            redisTemplate.delete(MAX_PARTICIPANTS_KEY);
            redisTemplate.delete(PRIZE_KEY);
            redisTemplate.delete(GAME_ENDED_KEY);

            // 重新初始化默认值
            redisTemplate.opsForValue().set(NUM_STUDENTS_KEY, 0);
            redisTemplate.opsForValue().set(MAX_PARTICIPANTS_KEY, 10); // 默认参与人数为 10
            redisTemplate.opsForValue().set(GAME_ENDED_KEY, false); // 初始化游戏结束标志

            return new NumberResponse("success", "游戏已重置，可以重新开始。", null, 0.0, 0, getMaxParticipants(), 0);
        } catch (Exception e) {
            return new NumberResponse("error", "服务器错误", null, 0.0, 0, getMaxParticipants(), 0);
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
}