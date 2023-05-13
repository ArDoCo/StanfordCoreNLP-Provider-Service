/* Licensed under MIT 2023. */
package stanfordnlp.dtoconverter;

import edu.kit.kastel.mcse.ardoco.core.api.data.text.*;
import io.github.ardoco.textproviderjson.dto.*;
import io.github.ardoco.textproviderjson.textobject.DependencyImpl;
import org.eclipse.collections.api.list.ImmutableList;
import stanfordnlp.corenlp.PhraseImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectToDtoConverter {

    private static final String TREE_SEPARATOR = " ";
    private static final char TREE_OPEN_BRACKET = '(';
    private static final char TREE_CLOSE_BRACKET = ')';

    /**
     * converts an ArDoCo text into a text DTO
     * 
     * @param text the ArDoCo text
     * @return the text DTO
     */
    public TextDTO convertTextToDTO(Text text) {
        TextDTO textDTO = new TextDTO();
        List<SentenceDTO> sentences = generateSentenceDTOs(text.getSentences());
        textDTO.setSentences(sentences);
        return textDTO;
    }

    private List<SentenceDTO> generateSentenceDTOs(ImmutableList<Sentence> sentences) {
        return new ArrayList<>(sentences.toList().stream().map(this::convertToSentenceDTO).toList());
    }

    private SentenceDTO convertToSentenceDTO(Sentence sentence) {
        SentenceDTO sentenceDTO = new SentenceDTO();
        sentenceDTO.setSentenceNo(sentence.getSentenceNumber() + (long) 1);
        sentenceDTO.setText(sentence.getText());
        List<WordDTO> words = generateWordDTOs(sentence.getWords());
        sentenceDTO.setWords(words);

        Phrase rootPhrase = sentence.getPhrases().get(0);
        String tree = convertToConstituencyTrees(rootPhrase);
        sentenceDTO.setConstituencyTree(tree);
        return sentenceDTO;
    }

    private List<WordDTO> generateWordDTOs(ImmutableList<Word> words) {
        return new ArrayList<>(words.toList().stream().map(this::convertToWordDTO).toList());
    }

    private WordDTO convertToWordDTO(Word word) {
        WordDTO wordDTO = new WordDTO();
        wordDTO.setId(word.getPosition() + (long) 1);
        wordDTO.setText(word.getText());
        wordDTO.setLemma(word.getLemma());
        try {
            wordDTO.setPosTag(PosTag.forValue(word.getPosTag().toString()));
        } catch (IOException e) {
            return null;
        }
        wordDTO.setSentenceNo(word.getSentenceNo() + (long) 1);
        List<DependencyImpl> inDep = new ArrayList<>();
        List<DependencyImpl> outDep = new ArrayList<>();
        for (DependencyTag depType : DependencyTag.values()) {
            ImmutableList<Word> inDepWords = word.getIncomingDependencyWordsWithType(depType);
            inDep.addAll(inDepWords.toList().stream().map(x -> new DependencyImpl(depType, x.getPosition())).toList());
            ImmutableList<Word> outDepWords = word.getOutgoingDependencyWordsWithType(depType);
            outDep.addAll(outDepWords.toList().stream().map(x -> new DependencyImpl(depType, x.getPosition())).toList());
        }
        List<IncomingDependencyDTO> inDepDTO = generateDepInDTOs(inDep);
        List<OutgoingDependencyDTO> outDepDTO = generateDepOutDTOs(outDep);
        wordDTO.setIncomingDependencies(inDepDTO);
        wordDTO.setOutgoingDependencies(outDepDTO);
        return wordDTO;
    }

    private String convertToConstituencyTrees(Phrase rootPhrase) {
        return convertToSubtree(rootPhrase);
    }

    private String convertToSubtree(Phrase phrase) {
        List<Word> words = phrase.getContainedWords().toList().stream().filter(x -> x.getPhrase().equals(phrase)).collect(Collectors.toList());
        StringBuilder constituencyTree = new StringBuilder().append(TREE_OPEN_BRACKET);
        constituencyTree.append(phrase.getPhraseType().toString());
        List<Phrase> subphrases = new ArrayList<>(((PhraseImpl)phrase).getChildPhrases().castToList());
        // since we don't know the order of words and subphrases we have to reconstruct the order by comparing the word index
        while (!words.isEmpty() || !subphrases.isEmpty()) {
            if (subphrases.isEmpty()) {
                // word next
                Word word = words.remove(0);
                constituencyTree.append(TREE_SEPARATOR).append(convertWordToTree(word));
            } else if (words.isEmpty()) {
                // phrase next
                Phrase subphrase = subphrases.remove(0);
                constituencyTree.append(TREE_SEPARATOR).append(convertToSubtree(subphrase));
            } else {
                int wordIndex = words.get(0).getPosition();
                List<Integer> phraseWordIndices = subphrases.get(0).getContainedWords().toList().stream().map(Word::getPosition).toList();
                if (wordIndex < Collections.min(phraseWordIndices)) {
                    // word next
                    Word word = words.remove(0);
                    constituencyTree.append(TREE_SEPARATOR).append(convertWordToTree(word));
                } else {
                    // phrase next
                    Phrase subphrase = subphrases.remove(0);
                    constituencyTree.append(TREE_SEPARATOR).append(convertToSubtree(subphrase));
                }
            }
        }
        constituencyTree.append(TREE_CLOSE_BRACKET);
        return constituencyTree.toString();
    }

    private String convertWordToTree(Word word) {
        return TREE_OPEN_BRACKET + word.getPosTag().toString() + TREE_SEPARATOR + word.getText() + TREE_CLOSE_BRACKET;
    }

    private List<IncomingDependencyDTO> generateDepInDTOs(List<DependencyImpl> dependencies) {
        return new ArrayList<>(dependencies.stream().map(this::convertToDepInDTO).toList());
    }

    private List<OutgoingDependencyDTO> generateDepOutDTOs(List<DependencyImpl> dependencies) {
        return new ArrayList<>(dependencies.stream().map(this::convertToDepOutDTO).toList());
    }

    private IncomingDependencyDTO convertToDepInDTO(DependencyImpl dependency) {
        IncomingDependencyDTO dependencyDTO = new IncomingDependencyDTO();
        dependencyDTO.setDependencyTag(dependency.getDependencyTag());
        dependencyDTO.setSourceWordId(dependency.getWordId() + 1);
        return dependencyDTO;
    }

    private OutgoingDependencyDTO convertToDepOutDTO(DependencyImpl dependency) {
        OutgoingDependencyDTO dependencyDTO = new OutgoingDependencyDTO();
        dependencyDTO.setDependencyTag(dependency.getDependencyTag());
        dependencyDTO.setTargetWordId(dependency.getWordId() + 1);
        return dependencyDTO;
    }
}
