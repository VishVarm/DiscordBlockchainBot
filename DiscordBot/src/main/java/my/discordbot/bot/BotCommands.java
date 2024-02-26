package my.discordbot.bot;

import my.discordbot.blockchainsrc.Block;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import my.discordbot.blockchainsrc.BlockChain;
import java.util.Timer;
import java.util.Random;
import java.util.TimerTask;

public class BotCommands extends ListenerAdapter {

    //public BlockChain blockChain;
    public boolean questionAsked = false;
    public int answer;
    private String prevHash = "0";
    private int transactionId = 0;
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("start")) {
            event.reply("It worked").queue();
        } else if (event.getName().equals("generatewallet")) {
            String id = event.getMember().getId();
            String keys = BlockChain.generateWallet(id);
            if (keys.equals("Member already has wallet")) {
                event.reply(event.getMember().getAsMention() + " already has wallet").setEphemeral(true).queue();
            } else {
                event.reply("Wallet Generated for " + event.getMember().getAsMention()).queue();
            }
        } else if (event.getName().equals("getbalance")) {
            String id = event.getMember().getId();
            if (BlockChain.wallets.get(id) == null) {
                event.reply("No balance as you don't have a wallet. Create a wallet with the /generatewallet command.").setEphemeral(true).queue();
            } else {
                event.reply("Wallet Balance: " + BlockChain.wallets.get(id).getBalance()).setEphemeral(true).queue();
            }
        } else if (event.getName().equals("answer")) {
            if (!BlockChain.wallets.containsKey(event.getMember().getId())) {
                event.reply(event.getMember().getAsMention() + " create a wallet with the /generatewallet command to answer this question and be rewarded.").setEphemeral(true).queue();
            }
            OptionMapping option = event.getOption("answer");
            if (option == null) {
                event.reply("Error, your answer did not go through").setEphemeral(true).queue();
                return;
            }
            int userAnswer = option.getAsInt();
            if (userAnswer == answer) {
                System.out.println("Over hereeee");
                questionAsked = false;
                Block block = new Block(prevHash, transactionId);
                transactionId++;
                BlockChain.addBlock(block, BlockChain.wallets.get(event.getMember().getId()));
                event.reply("Congratulations! " + event.getMember().getAsMention() + " has received 10 coins!").queue();
            } else {
                event.reply("Sorry " + event.getMember().getAsMention() + " but that is incorrect.").setEphemeral(true).queue();
            }
        } else if (event.getName().equals("pay")) {
            OptionMapping recipient = event.getOption("recipient");
            OptionMapping option2 = event.getOption("amount");
            if (option2 == null || recipient == null) {
                event.reply("Error, your inputs did not go through").setEphemeral(true).queue();
                return;
            }
            int amount = option2.getAsInt();
            if (recipient.getAsMember() == null) {
                event.reply("Recipient must be a member of this server").setEphemeral(true).queue();
            }
            if (BlockChain.wallets.get(event.getMember().getId()) == null) {
                event.reply("You must have a wallet to send coins").setEphemeral(true).queue();
            }
            if (BlockChain.wallets.get(recipient.getAsMember().getId()) == null) {
                event.reply("Recipient must have a wallet").setEphemeral(true).queue();
            }
            if (amount > BlockChain.wallets.get(event.getMember().getId()).getBalance()) {
                event.reply("You can't send more coins than you have currently").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (questionAsked) {
            return;
        }
        Random rand = new Random();
        int x1 = rand.nextInt(10);
        int x2 = rand.nextInt(10);
        answer = x1 * x2;
        event.getChannel().sendMessage("What is " + x1 + " * " + x2).queue();
        questionAsked = true;

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                questionAsked = false;
                event.getChannel().sendMessage("No one got the correct answer").queue();
            }
        };
        timer.schedule(task, 1200000);

    }

}
