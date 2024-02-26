package my.discordbot.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import my.discordbot.blockchainsrc.BlockChain;
import java.util.Random;


public class DiscordBot {


    public static void main(String[] args) {

        JDA bot = JDABuilder.createDefault("MTIwNTAwNzA0MDk1MTM2MTUzNg.GOUmgt._aVxMu97wDgiA3M5c_gESuqY9Elt3mPmjuMbJQ")
                .setActivity(Activity.playing("Poker"))
                .addEventListeners(new BotCommands())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        try {
            bot.awaitReady();
        } catch (InterruptedException e) {
            System.out.println("error");
        }

        Guild guild = bot.getGuildById("1205008540981534720");
        if (guild != null) {
            guild.upsertCommand("start", "test command").queue();
            guild.upsertCommand("generatewallet", "Generate Your Wallet").queue();
            guild.upsertCommand("getbalance", "Get the Balance of your Wallet").queue();
            guild.upsertCommand("answer", "Answer the Bot's Question for Coins!")
                    .addOption(OptionType.INTEGER, "answer", "Your Answer to the Question", true)
                    .queue();
            guild.upsertCommand("pay", "Send another user coins")
                    .addOption(OptionType.USER, "recipient", "User you want to send Coins to", true)
                    .addOption(OptionType.INTEGER, "amount", "Amount of Coins to Send", true)
                    .queue();
        }

    }

}
