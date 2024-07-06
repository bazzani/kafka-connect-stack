package com.bevans.kafka.connect.springboot;

import com.bevans.kafka.connect.springboot.data.user.UserData;
import com.bevans.kafka.connect.springboot.data.user.UserDataRepository;
import com.bevans.kafka.connect.springboot.user.UserDataKafkaConsumerFixture;
import com.bevans.kafka.connect.springboot.user.UserDataKafkaTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThatIterable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(UserDataKafkaTestConfig.class)
public class UserDataJsonSchemaSourceConnectorTest {
    private final UserDataRepository userDataRepository;
    private final UserDataKafkaConsumerFixture userDataKafkaConsumerFixture;

    @Autowired
    public UserDataJsonSchemaSourceConnectorTest(UserDataRepository userDataRepository,
                                                 UserDataKafkaConsumerFixture userDataKafkaConsumerFixture) {
        this.userDataRepository = userDataRepository;
        this.userDataKafkaConsumerFixture = userDataKafkaConsumerFixture;
    }

    @BeforeEach
    public void setup() throws InterruptedException {
        userDataKafkaConsumerFixture.clearRecords();
        userDataRepository.deleteAll();
        Thread.sleep(500L);
    }

    @Test
    void shouldAddUserDataToDatabase() {
        // given
        var userToSave = UserData.builder()
                .name("Walter Hartwell White Sr.")
                .age(51)
                .build();

        // when
        userDataRepository.save(userToSave);

        // then
        var allUserData = userDataRepository.findAll();
        assertThatIterable(allUserData).hasSize(1);

        var userDataList = StreamSupport
                .stream(allUserData.spliterator(), false)
                .toList();
        var savedUser = userDataList.get(0);

        assertAll(
                () -> assertThat(savedUser.getName()).isEqualTo(userToSave.getName()),
                () -> assertThat(savedUser.getAge()).isEqualTo(userToSave.getAge())
        );
    }

    @Test
    void shouldAddGoodUserDataToDatabaseAndFindRecordOnTopic() throws InterruptedException {
        // given
        var goodUser = UserData.builder()
                .name("Walter Hartwell White Sr.")
                .age(51)
                .build();
        saveUserAndTakeANap(goodUser);

        // when
        var latestKafkaRecord = userDataKafkaConsumerFixture.getLatestKafkaRecord();

        // then
        assertTrue(latestKafkaRecord.isPresent());

        var consumerRecord = latestKafkaRecord.get();
        var userFromTopic = new ObjectMapper().convertValue(consumerRecord.value(), UserData.class);
        assertAll(
                () -> assertThat(userFromTopic.getName()).isEqualTo(goodUser.getName()),
                () -> assertThat(userFromTopic.getAge()).isEqualTo(goodUser.getAge())
        );
    }

    @Test
    void shouldAddBadUserDataToDatabaseAndNotFindRecordOnTopic() throws InterruptedException {
        // given
        var badUser = UserData.builder()
                .name("Walter White is really Heisenberg, he is the one who knocks!") // name is too long
                .age(126)                                                             // age is too old
                .build();
        saveUserAndTakeANap(badUser);

        // when
        var latestKafkaRecord = userDataKafkaConsumerFixture.getLatestKafkaRecord();

        // then
        assertFalse(latestKafkaRecord.isPresent());
    }

    private void saveUserAndTakeANap(UserData userDataToSave) throws InterruptedException {
        userDataRepository.save(userDataToSave);
        Thread.sleep(1000L);
    }
}
