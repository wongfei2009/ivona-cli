package ivonacli;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;

import com.ivonacloud.aws.auth.BasicAWSCredentials;
import com.ivonacloud.services.tts.IvonaSpeechCloudClient;
import com.ivonacloud.services.tts.model.CreateSpeechRequest;
import com.ivonacloud.services.tts.model.CreateSpeechResult;
import com.ivonacloud.services.tts.model.Input;
import com.ivonacloud.services.tts.model.OutputFormat;
import com.ivonacloud.services.tts.model.Parameters;
import com.ivonacloud.services.tts.model.Voice;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class App {
	public static void main(String[] args) {
		OptionParser parser = new OptionParser("i:d:o:v:?*.");
		OptionSet options = parser.parse(args);

		if (options.has("i") || options.has("d")) {
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(
					"GDNAJWICHRX74PODJHOA",
					"g069TLxvBph6ZnOGSfmveyisTVjZ9odzF0UCkETJ");
			IvonaSpeechCloudClient client = new IvonaSpeechCloudClient(awsCreds);
			client.setEndpoint("https://tts.eu-west-1.ivonacloud.com", "tts",
					"eu-west-1");
			CreateSpeechRequest createSpeechRequest = new CreateSpeechRequest();
			Input input = new Input();
			input.setType("application/ssml+xml");

			String data = "Mary has a little lamb. Alice has a black cat.";
			if (options.has("d") && options.hasArgument("d")) {
				data = (String) options.valueOf("d");
			} else if (options.has("i") && options.hasArgument("i")) {
				File inputFile = new File((String) options.valueOf("i"));
				try {
					data = FileUtils.readFileToString(inputFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			input.setData(data);
			createSpeechRequest.setInput(input);
			OutputFormat outputFormat = new OutputFormat();
			outputFormat.setCodec("MP3");
			outputFormat.setSampleRate((short) 22050);
			createSpeechRequest.setOutputFormat(outputFormat);
			Parameters parameters = new Parameters();
			parameters.setParagraphBreak((short) 800);
			parameters.setRate("x-fast");
			parameters.setSentenceBreak((short) 200);
			parameters.setVolume("loud");
			createSpeechRequest.setParameters(parameters);
			Voice voice = new Voice();
			if (options.has("v") 
					&& options.hasArgument("v") 
					&& "female".compareToIgnoreCase((String)options.valueOf("v")) == 0) {
				voice.setGender("Female");
				voice.setLanguage("en-US");
				voice.setName("Kimberly");
			} else {
				voice.setGender("Male");
				voice.setLanguage("en-US");
				voice.setName("Joey");
			}
			createSpeechRequest.setVoice(voice);
			CreateSpeechResult result = client
					.createSpeech(createSpeechRequest);

			String outputFileName = "output.mp3";

			if (options.has("o") && options.hasArgument("o")) {
				outputFileName = (String) options.valueOf("o");
			}
			try {
				File file = new File(outputFileName);
				Files.copy(result.getBody(), file.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				System.out.println("MP3 is successfully saved to "
						+ file.toPath().toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("IVONA Text-to-speech CLI");
			System.out
					.println("	Usage 1: -i <text filename> [-o <output filename>] [-v male|female]");
			System.out
					.println("	Usage 2: -d <text> [-o <output filename>] [-v male|female]");
		}
	}
}
