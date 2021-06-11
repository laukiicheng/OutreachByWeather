// package com.acme.outreach.validators
//
// import com.acme.outreach.NotPastDateValidator
// import io.kotest.data.blocking.forAll
// import io.kotest.data.row
// import io.kotest.matchers.shouldBe
// import io.kotest.provided.BaseStringSpec
// import io.mockk.Runs
// import io.mockk.clearAllMocks
// import io.mockk.every
// import io.mockk.just
// import io.mockk.mockk
// import io.mockk.verify
// import java.util.Calendar
// import java.util.Date
// import javax.validation.ConstraintValidatorContext
//
// internal class NotPastDateValidatorTest : BaseStringSpec() {
//     init {
//
//         val isValidName = NotPastDateValidator::isValid.name
//
//         val builder = mockk<ConstraintValidatorContext.ConstraintViolationBuilder>()
//         val context = mockk<ConstraintValidatorContext> {
//             every { disableDefaultConstraintViolation() } just Runs
//             every { buildConstraintViolationWithTemplate(any()) } returns builder
//         }
//         every { builder.addConstraintViolation() } returns context
//
//         val validator = NotPastDateValidator()
//
//         "$isValidName should return true when date is null" {
//             validator.isValid(null, context) shouldBe true
//         }
//
//         "$isValidName should return true when date not in the past" {
//             forAll(
//                 // row(Date())
//                 row(
//                     Calendar.getInstance().apply {
//                         time = Date()
//                         add(Calendar.DATE, 10)
//                     }.time
//                 )
//             ) { date: Date? ->
//                 clearAllMocks(answers = false)
//
//                 validator.isValid(date, context) shouldBe true
//
//                 verify(exactly = 0) {
//                     context.disableDefaultConstraintViolation()
//                     context.buildConstraintViolationWithTemplate(any())
//                 }
//             }
//         }
//
//         "$isValidName should return false when date is in the past" {
//             val cal = Calendar.getInstance().apply {
//                 time = Date()
//                 add(Calendar.DATE, -10)
//             }
//
//             validator.isValid(cal.time, context) shouldBe false
//
//             verify(exactly = 1) {
//                 context.disableDefaultConstraintViolation()
//                 context.buildConstraintViolationWithTemplate(any())
//             }
//         }
//     }
// }
