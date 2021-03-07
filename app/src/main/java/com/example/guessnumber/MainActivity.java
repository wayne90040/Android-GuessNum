package com.example.guessnumber;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private TextView[] inputView = new TextView[4];  // 輸入 4 個數
    private int[] intputRes = {R.id.main_input1, R.id.main_input2, R.id.main_input3, R.id.main_input4};

    private ImageButton[] numberButton = new ImageButton[10];
    private int[] numberRes = {R.id.main_btn_0, R.id.main_btn_1, R.id.main_btn_2, R.id.main_btn_3, R.id.main_btn_4,
            R.id.main_btn_5, R.id.main_btn_6, R.id.main_btn_7, R.id.main_btn_8, R.id.main_btn_9
    };

    private ListView resultListView;
    private SimpleAdapter adapter;
    private String[] from = {"order", "guess", "result"};
    private int[] to = {R.id.item_order, R.id.item_guess, R.id.item_result};
    private LinkedList<HashMap<String, String>> hist;

    private LinkedList<Integer> answer = new LinkedList<>();  // 題目答案
    private LinkedList<Integer> inputValue = new LinkedList<>();  // user 輸入
    private int inputPoint;  // 輸入指標位置 0 - 3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        createNewGame();
    }

    private void initView() {
        for (int i = 0; i < inputView.length; i++) {  // 綁定
            inputView[i] = findViewById(intputRes[i]);
        }

        for (int i = 0; i < numberButton.length; i++) {
            numberButton[i] = findViewById(numberRes[i]);
        }

        resultListView = findViewById(R.id.main_listview);
        hist = new LinkedList<>();
        adapter = new SimpleAdapter(this, hist, R.layout.result_listview_item, from, to);
        resultListView.setAdapter(adapter);
    }

    private void createNewGame() {
        answer = createAnswer();  // 產生新題目
        clearAction();  // 清除紀錄
    }

    public void didTapNumber(View view) {
        if (inputPoint == 4) return;  // 按滿4位數 即 return

        for (int i = 0; i < numberButton.length; i++) {

            if (view == numberButton[i]) {  // 逐一比對是按到哪個 view -> number button
                inputValue.set(inputPoint, i);  // set Value
                inputView[inputPoint].setText("" + i);  // update UI -> 更新對應位數的View
                inputPoint++;
                numberButton[i].setEnabled(false);
                break;
            }
        }
    }

    public void didTapSend(View view) {
        if (inputPoint != 4) return;  // 4 位數才可 Send

        int a, b; a = b = 0; String guess = "";  // 幾 A 幾 B

        for (int i = 0; i < inputValue.size(); i++) {
            guess += inputValue.get(i);  // 把猜的數字拼起來
            if (inputValue.get(i).equals(answer.get(i))) {
                a++;
            }
            else if (answer.contains(inputValue.get(i))) {
                b++;
            }
        }

        clearAction();  // 清除UI -> 再次輸入

        // 顯示紀錄 在 ListView 上
        HashMap<String, String> row_data = new HashMap<>();
        row_data.put(from[0], "" + (hist.size() + 1));
        row_data.put(from[1], guess);
        row_data.put(from[2], a + "A" + b + "B");
        hist.add(row_data);
        adapter.notifyDataSetChanged();
        resultListView.smoothScrollToPosition(hist.size() - 1);

        // 印出結果
        if (a == 4) {
            // win
            displayResult(true);
        }
        else if (hist.size() == 10) {
            // lose
            displayResult(false);
        }
    }

    public void didTapReplay(View view) {
        replayNewGame();
    }

    public void didTapBack(View view) {
        if (inputPoint == 0) return;

        inputPoint --;
        inputView[inputPoint].setText("");
        numberButton[inputValue.get(inputPoint)].setEnabled(true);  // 把對應的 數字按鈕 enable 打開
        inputValue.set(inputPoint, -1);
    }

    public void didTapClear(View view) {
        clearAction();
    }

    // 產生新題目
    private LinkedList<Integer> createAnswer() {
        LinkedList<Integer> res = new LinkedList<>();
        HashSet<Integer> nums = new HashSet<>();  // HashSet -> 不重複 (無順序性)

        while (nums.size() < 4) {
            nums.add((int)(Math.random() * 10));
        }

        for (Integer num: nums) {
            res.add(num);
        }

        Collections.shuffle(res);  // 隨機打亂順序

        System.out.println(res);
        return  res;
    }

    // Clear Action
    private void clearAction() {
        inputPoint = 0;
        inputValue.clear();

        for (int i = 0; i < 4; i++) {
            inputValue.add(-1);
        }

        for (TextView t: inputView) {
            t.setText("");
        }

        for (ImageButton b: numberButton) {  // button 重啟
            b.setEnabled(true);
        }
    }

    private void replayNewGame() {
        createNewGame();
        hist.clear();
        adapter.notifyDataSetChanged();
    }

    // display result
    private void displayResult(boolean isWinner) {
        // 互動對話方塊
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("遊戲結果");

        StringBuffer ansString = new StringBuffer();
        for (int i = 0; i < 4; i ++) ansString.append(answer.get(i));

        builder.setMessage( isWinner? "完全正確" : "調戰失敗\n" + "答案" + ansString);

        builder.setPositiveButton("開新局", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // replay
                replayNewGame();
            }
        });

        builder.create().show();
    }
}