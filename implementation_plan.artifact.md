# Fix Font Family Type Mismatch

This plan addresses a build error where `FontFamily` is provided to a `CompositionLocal` that expects `SystemFontFamily`. The error occurs because `staticCompositionLocalOf { FontFamily.Default }` infers the more specific `SystemFontFamily` type, but the theme passes a generic `FontFamily`.

## User Review Required

> [!NOTE]
> This is a low-risk fix that explicitly types the `LocalAurafeedFontFamily` to allow any `FontFamily` implementation (including custom fonts and system fonts) to be provided through the theme.

## Proposed Changes

### UI Theme Component

#### [MODIFY] [Type.kt](file:///C:/Users/AC/AndroidStudioProjects/Aurafeed/shared/src/commonMain/kotlin/com/nidoham/aurafeed/ui/theme/Type.kt)

- Update `LocalAurafeedFontFamily` declaration to explicitly use the `FontFamily` type.

## Verification Plan

### Automated Tests
- Run `./gradlew :shared:assemble` to verify the project builds without the type mismatch error.

### Manual Verification
- None required beyond a successful build, as this is a compile-time fix.
