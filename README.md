# CrolClient

Визуальный клиент для Minecraft 1.21.4 (Fabric). **Не является чит-клиентом**:
в проекте намеренно отсутствуют KillAura, Reach, Velocity, AutoClicker, X-Ray,
автосвапы предметов, China Hat, rotation/hitbox-индикаторы и любые другие
PvP-ассисты, дающие нечестное игровое преимущество.

## Технический стек

| Компонент      | Версия              | Источник для проверки |
|-----------------|---------------------|------------------------|
| Minecraft       | 1.21.4              | https://fabricmc.net/develop/ |
| Fabric Loader   | 0.16.10             | https://maven.fabricmc.net/net/fabricmc/fabric-loader/ |
| Fabric API      | 0.119.4+1.21.4      | https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/ |
| Yarn mappings   | 1.21.4+build.8      | https://maven.fabricmc.net/net/fabricmc/yarn/ |
| Fabric Loom     | 1.9.2               | https://maven.fabricmc.net/net/fabricmc/fabric-loom/ |
| Gradle          | 8.10.2              | https://gradle.org/releases/ |
| Java            | 21 (Temurin)        | https://adoptium.net/ |

Все версии зафиксированы в `gradle.properties` с комментариями и ссылками —
перепроверяйте их вручную при обновлении проекта.

## Сборка локально

```bash
git clone <repo>
cd crolclient
# если gradle-wrapper.jar отсутствует — сгенерируйте его один раз:
gradle wrapper --gradle-version 8.10.2
./gradlew build
```

Собранный jar появится в `build/libs/crolclient-1.0.0.jar`.

## Запуск клиента для разработки

```bash
./gradlew runClient
```

## Архитектура

- `module/Module.java` — базовый класс всех модулей. Инструкция по добавлению
  нового модуля — прямо в javadoc класса.
- `module/ModuleManager.java` — реестр модулей, поиск, обработка хоткеев.
- `theme/ThemeManager.java` — загрузка тем из `assets/crolclient/themes/*.json`.
- `config/ConfigManager.java` — сохранение/загрузка состояния через Gson.
- `gui/ClickGuiScreen.java` — экран меню (Right Shift или `/crol`).
- `auction/` — FunTime Auction Helper: асинхронный сканер лотов аукциона,
  парсер цены из lore, база истории цен с медианой/средним, оверлей подсветки.

## Команды

- `/crol` — открыть ClickGUI
- `/crol search <запрос>` — поиск модулей
- `/crol toggle <модуль>` — включить/выключить модуль
- `/crol waypoint add <имя>` — сохранить точку с текущих координат
- `/crol waypoint remove <имя>` — удалить точку
- `/crol waypoint list` — список точек

## Лицензия

MIT — см. `LICENSE`.
