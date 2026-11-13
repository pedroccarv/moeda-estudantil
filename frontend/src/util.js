export function destino(papel) {
  if (papel === 'PROFESSOR') return '/professor'
  if (papel === 'EMPRESA') return '/empresa'
  return '/aluno'
}

export function rotuloPapel(papel) {
  if (papel === 'PROFESSOR') return 'professor'
  if (papel === 'EMPRESA') return 'empresa'
  return 'aluno'
}

export function quando(iso) {
  if (!iso) return ''
  const data = new Date(iso)
  if (Number.isNaN(data.getTime())) return String(iso)
  return data.toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export function fotoSegura(url) {
  return typeof url === 'string' && url.startsWith('/uploads/') ? url : ''
}
