import { parseTime } from '@/utils/index'
import { APPROVAL_STATUS } from '@/common/dict'
export function formatTime(val) {
  if (val) {
    return parseTime(new Date(val), '{y}-{m}-{d}')
  }
  return ''
}
// 格式化审批状态
export function formatApproveStatus(val) {
  const data = APPROVAL_STATUS.find(item => item.value === val)
  return data ? data.label : ''
}
