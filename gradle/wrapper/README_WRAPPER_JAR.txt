Этот файл — временная заглушка.
gradle-wrapper.jar генерируется автоматически в GitHub Actions
(см. .github/workflows/build.yml, шаг "Provision Gradle Wrapper jar")
перед сборкой, поэтому commit пустого jar в репозиторий не требуется.

Если хотите собрать проект локально и у вас ещё нет gradle-wrapper.jar:
1. Установите Gradle 8.10.2 вручную (https://gradle.org/releases/)
   или через SDKMAN: sdk install gradle 8.10.2
2. В корне проекта выполните:
     gradle wrapper --gradle-version 8.10.2
   Это сгенерирует настоящий gradle-wrapper.jar на месте этого файла.
3. Дальше пользуйтесь ./gradlew как обычно.
