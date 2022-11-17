import { parseTime } from '@/utils/index'
export function formatTime(val) {
  if (val) {
    return parseTime(new Date(val), '{y}-{m}-{d}')
  }
  return ''
}

export function formatTimeFull(val) {
  if (val) {
    return parseTime(new Date(val), '{y}-{m}-{d} {h}:{i}:{s}')
  }
  return ''
}
