# Hudder

A highly customizable minecraft mod which allows you to fully design your own hud using either hudder's simple programming language or javascript.

Want to do math to show icons on your screen? Want to use JavaScript and/or hudder's own custom language?! then use hudder!

### Available features:
- [x] Custom image rendering
- [x] Variables
- [x] Complex Arithmetic Operators
- [x] Removal and complete replacement of the hotbar.
- [x] Methods
- [x] Console output
- [ ] User defined methods

Hudder allows you to create a custom hud to appear on your screen in game using it's very simple language (Or JavaScript if you know how to use it)!



### Example:


`{fps}` will produce:

![150fps](https://cdn.modrinth.com/data/cached_images/e94855b58a39e5ef6c293f2a1d2c2eedfd6545ca.png)

### You can learn more on the [wiki](https://ngspace.github.io/hudder/)

### Full Variable list can also be found [Here](https://ngspace.github.io/hudder/varlist).
### Full Method list can also be found [Here](https://ngspace.github.io/hudder/methodlist).


# How to Run, Compile and Add Hudder as a gradle dependency

## Compiling
Batch (Windows):
```batchfile
gradlew build
```

Unix (Mac/Linux):
```shell
./gradlew build
```


## Running
Batch (Windows):
```batchfile
gradlew runClient
```

Unix (Mac/Linux):
```shell
./gradlew runClient
```

## Adding as a dependency

In your `build.gradle` add:

```gradle
repositories {
	//...
	exclusiveContent {
		forRepository {
			maven {
				name = "Modrinth"
				url = "https://api.modrinth.com/maven"
			}
		}
		filter {
			includeGroup "maven.modrinth"
		}
	}
}
//...
dependencies {
	modImplementation "maven.modrinth:hudder:[Hudder Version]-[Minecraft version]"
}
```
