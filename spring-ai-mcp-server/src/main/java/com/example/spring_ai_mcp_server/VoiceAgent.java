// VoiceAgent.java
public class VoiceAgent {
    String stt(String audio) { return "Mala Jio recharge karaycha ahe"; } // Speech to Text
        String llm(String text) { return "Jio 349 best ahe, karu ka?"; }
            void tts(String text) { System.out.println("[Speaking]: " + text); } // Text to Speech

                void run(String audioInput) {
                        String text = stt(audioInput);
                                String reply = llm(text);
                                        tts(reply);
                                            }
                                                public static void main(String[] a) {
                                                        new VoiceAgent().run("audio_file.wav");
                                                            }
                                                            }