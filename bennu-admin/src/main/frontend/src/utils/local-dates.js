import dayjs from 'dayjs'
import Utc from 'dayjs/plugin/utc'
dayjs.extend(Utc)

export const dateTimeWithUtc = ({ isoDate, locale, format }) => {
  const localDate = dayjs(isoDate).local()
  const localTimeOffset = localDate.utcOffset()
  if (localTimeOffset === 0) { // if current timezone is GMT
    return `${localDate.locale(locale).format(format)} (GMT)`
  } else {
    const utcDate = dayjs.utc(isoDate)
    if (utcDate.locale(locale).format('L') === localDate.locale(locale).format('L')) {
      // same day, omit date
      return `${localDate.locale(locale).format(format)} (${utcDate.locale(locale).format('LT')} GMT)`
    } else {
      // different day, show date and time
      return `${localDate.locale(locale).format(format)} (${utcDate.locale(locale).format('L LT')} GMT)`
    }
  }
}

export const dateTimeWithUtcOffset = ({ isoDate, locale, format }) => {
  const localDate = dayjs(isoDate).local()
  const localTimeOffset = localDate.utcOffset()
  if (localTimeOffset === 0) { // if current timezone is GMT
    return `${localDate.locale(locale).format(format)} (GMT)`
  } else {
    return `${localDate.locale(locale).format(format)} (GMT${localDate.locale(locale).format('Z')})`
  }
}

export const utcDateTime = ({ isoDate, locale, format }) => {
  const utcDate = dayjs.utc(isoDate)
  return `${utcDate.locale(locale).format(format)} GMT`
}
