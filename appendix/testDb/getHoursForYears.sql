select sum(hours) from months
left join days 
on (months.id = days.monthId)
where months.yearId = 1
