package com.example.demo;

import com.example.demo.model.Game;
import com.example.demo.model.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameTests {


	@Test
	public void testStartGame(){
		Game game = new Game("mychannel", "TestPlayer1", "TestPlayer2");

		Response response = game.start();

		Assert.assertTrue(game.getPlayer1().getSymbol() == 'X');
		Assert.assertTrue(game.getPlayer2().getSymbol() == 'O');
		Assert.assertNotNull(response);
		Assert.assertNull(response.getWinner());
		Assert.assertNotNull(response.getBoard());

	}

	@Test
	public void testPlayGame(){
		Game game = new Game("mychannel", "TestPlayer1", "TestPlayer2");

		Response response = game.start();
		Response playResponse = game.play(game.getPlayer1(), 0,0);

		Assert.assertTrue(game.getPlayer1().getSymbol() == 'X');
		Assert.assertTrue(game.getPlayer2().getSymbol() == 'O');
		Assert.assertNotNull(playResponse);
		Assert.assertNull(playResponse.getWinner());
		Assert.assertNotNull(playResponse.getBoard());
		Assert.assertTrue(playResponse.getBoard()[0][0] == 'X');

	}

}
