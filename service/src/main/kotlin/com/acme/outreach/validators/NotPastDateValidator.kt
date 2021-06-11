// package com.acme.outreach
//
// import java.util.Date
// import javax.validation.Constraint
// import javax.validation.ConstraintValidator
// import javax.validation.ConstraintValidatorContext
// import javax.validation.Payload
// import kotlin.reflect.KClass
//
// class NotPastDateValidator : ConstraintValidator<IsNotPastDate, Date> {
//     override fun isValid(value: Date?, context: ConstraintValidatorContext): Boolean {
//         if (value == null) {
//             // This is handled by @field:NotNull
//             return true
//         }
//
//         context.apply {
//             disableDefaultConstraintViolation()
//
//             buildConstraintViolationWithTemplate(
//                 "Date $value must not be in the past"
//             ).addConstraintViolation()
//         }
//
//         val currentDate = Date()
//         if (value >= Date()) {
//             return true
//         }
//
//         return false
//     }
// }
//
// @MustBeDocumented
// @Constraint(validatedBy = [NotPastDateValidator::class])
// @Target(AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
// @Retention
// annotation class IsNotPastDate(
//     val message: String = "Date must not be in the past",
//     val groups: Array<KClass<out Any>> = [],
//     val payload: Array<KClass<out Payload>> = []
// )
